package com.node5.settlementservice.settlement.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.common.exception.ExceptionResponseDto;
import com.node5.settlementservice.settlement.application.dto.JobExecutionInfo;
import com.node5.settlementservice.settlement.application.dto.SettlementSourceItem;
import com.node5.settlementservice.settlement.client.CatalogClient;
import com.node5.settlementservice.settlement.client.OrderClient;
import com.node5.settlementservice.settlement.domain.SettlementProcessStatus;
import com.node5.settlementservice.settlement.domain.SettlementSource;
import com.node5.settlementservice.settlement.domain.SettlementSourceRepository;
import com.node5.settlementservice.settlement.exception.SettlementException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.node5.settlementservice.settlement.exception.SettlementErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementInternalService {

    private final JobLauncher jobLauncher;
    private final Job shopSettlementJob;
    private final JobExplorer jobExplorer;
    private final SettlementSourceRepository settlementSourceRepository;
    private final CatalogClient catalogClient;
    private final OrderClient orderClient;
    private final ObjectMapper objectMapper;

    // 전체 판매자 월별 정산 배치 실행
    public Long runAll(String yearMonth) {
        JobParameters params = getDefaultSettlementParamsBuilder(yearMonth)
                .toJobParameters();

        try {
            return jobLauncher.run(shopSettlementJob, params).getId();
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new SettlementException(SETTLEMENT_ALREADY_COMPLETED);
        } catch (Exception e) {
            throw new SettlementException(BATCH_JOB_LAUNCH_FAILED, e.getMessage());
        }
    }

    // 특정 판매자 월별 정산 배치 실행
    public Long runShop(String shopId, String yearMonth) {
        JobParameters params = getDefaultSettlementParamsBuilder(yearMonth)
                .addString("shopId", shopId)
                .toJobParameters();

        try {
            return jobLauncher.run(shopSettlementJob, params).getId();
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new SettlementException(SETTLEMENT_ALREADY_COMPLETED);
        } catch (Exception e) {
            throw new SettlementException(BATCH_JOB_LAUNCH_FAILED, e.getMessage());
        }
    }

    // 단일 배치 실행 상태 조회
    public JobExecutionInfo getStatusByJobExecutionId(Long jobExecutionId) {
        // JobExplorer를 사용하여 DB 메타데이터에서 JobExecution 조회
        JobExecution execution = jobExplorer.getJobExecution(jobExecutionId);

        if (execution == null) {
            throw new IllegalArgumentException("JobExecutionId: " + jobExecutionId + "를 찾을 수 없습니다.");
        }

        // JobParameters에서 필요한 정보 추출
        JobParameters params = execution.getJobParameters();
        String shopId = params.getString("shopId");
        String startDate = params.getString("startDate");
        String endDate = params.getString("endDate");

        String period = startDate + " ~ " + endDate;

        // 실행 시간 및 duration 계산
        LocalDateTime start = execution.getStartTime();
        LocalDateTime end = execution.getEndTime() != null ? execution.getEndTime() : LocalDateTime.now();
        long duration = start != null ? ChronoUnit.MILLIS.between(start, end) : 0;

        // ExitStatus에서 상세 메시지 추출
        String exitDescription = execution.getExitStatus().getExitDescription();

        return JobExecutionInfo.from(execution, shopId, period, start, end, duration, exitDescription);
    }

    // Order -> SettlementSource 생성
    public void saveSettlementResource(List<SettlementSourceItem> items) {
        List<SettlementSource> sources = items.stream()
                .map(item -> SettlementSource.create(
                        item.productId(),
                        item.shopId(),
                        item.orderId(),
                        item.itemAmount(),
                        item.createdAt(),
                        SettlementProcessStatus.PENDING
                ))
                .toList();
        settlementSourceRepository.saveAll(sources);
    }

    // 기본 Job Parameter 생성
    private JobParametersBuilder getDefaultSettlementParamsBuilder(String yearMonth){
        YearMonth targetMonth;
        if(yearMonth != null && !yearMonth.isEmpty()){
            targetMonth = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        }else{
            // 지정하지 않을 경우 지난 달을 기본값으로 설정
            targetMonth = YearMonth.now().minusMonths(1);
        }

        // YearMonth 객체로 시작일, 종료일 계산
        String startDate = targetMonth.atDay(1).format(DateTimeFormatter.ISO_DATE);
        String endDate = targetMonth.atEndOfMonth().format(DateTimeFormatter.ISO_DATE);

        return new JobParametersBuilder()
                //.addLong("timestamp", System.currentTimeMillis())
                .addString("startDate", startDate)
                .addString("endDate", endDate);
    }

    public Boolean hasInProgressSettlement(List<UUID> shopIdList) {
        if (shopIdList == null || shopIdList.isEmpty()) {
            return false;
        }

        // 판매하는 상품 중 정산 대기 중인 것이 존재하는지 OrderItem 테이블 확인
        List<UUID> productIds = new ArrayList<>();
//                List.of(
//                    UUID.fromString("a10e8400-e29b-41d4-a716-446655440001"),
//                    UUID.fromString("a10e8400-e29b-41d4-a716-446655440002")
//                );
        // TODO List<UUID> productIds = catalogClient.getProductIdsByShopIds(shopIdList);
        if(!productIds.isEmpty()){
            try {
                ResponseEntity<Boolean> hasSettlementPending = orderClient.hasInProgressSettlementPending(productIds);
                if (Boolean.TRUE.equals(hasSettlementPending.getBody())) {
                    return true;
                }
            } catch(FeignException e) {
                throw new SettlementException(SETTLEMENT_FEIGN_ERROR, "message=" + getFeignErrorMessage(e));
            } catch(Exception e) {
                throw new SettlementException(SETTLEMENT_FEIGN_ERROR, "message=" + e.getMessage());
            }
        }

        // SettlementProcessStatus PENDING인 것 존재하는지 SettlementSource 테이블 확인
        return settlementSourceRepository.existsByShopIdInAndStatus(shopIdList, SettlementProcessStatus.PENDING);
    }

    private String getFeignErrorMessage(FeignException e) {
        ExceptionResponseDto err = parseFeignError(e);
        if (err != null) {
            return err.message();
        }
        return " (status:" + e.status() + ", raw:" + safeRawBody(e) + ")";
    }

    private ExceptionResponseDto parseFeignError(FeignException e) {
        return e.responseBody()
                .map(body -> {
                    try {
                        return objectMapper.readValue(e.contentUTF8(), ExceptionResponseDto.class);
                    } catch (Exception ex) {
                        return null;
                    }
                }).orElse(null);
    }

    private String safeRawBody(FeignException e) {
        try {
            return e.contentUTF8().isEmpty() ? "<empty>" : e.contentUTF8();
        } catch (Exception ex) {
            return "<unreadable>";
        }
    }
}
