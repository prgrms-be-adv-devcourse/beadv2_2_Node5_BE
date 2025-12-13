package com.node5.settlementservice.batch;

import com.node5.settlementservice.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class SettlementBatchConfig {
    private static final BigDecimal FEE_RATE = new BigDecimal("0.05"); // 수수료율 5% 고정

    // Job: 전체 정산 작업 정의
    @Bean
    public Job shopSettlementJob(JobRepository jobRepository, Step settlementStep) {
        return new JobBuilder("shopSettlementJob", jobRepository)
                .start(settlementStep)
                .build();
    }

    // Step: 정산 단계 Tasklet으로 정의
    @Bean
    public Step settlementStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            SettlementSourceRepository sourceRepository,
            SettlementResultRepository resultRepository
    ) {

        return new StepBuilder("settlementStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {

                    // [1] Job Parameter 추출 및 정산 대상 날짜 설정
                    Map<String, Object> jobParams = chunkContext.getStepContext().getJobParameters();
                    String shopParam = (String) jobParams.get("shopId");
                    LocalDate startDate = LocalDate.parse((String) jobParams.get("startDate"));
                    LocalDate endDate = LocalDate.parse((String) jobParams.get("endDate"));
                    Long batchId = chunkContext.getStepContext().getStepExecution().getJobExecutionId();

                    LocalDateTime startDateTime = startDate.atStartOfDay();
                    LocalDateTime endDateTimePlusOneDay = endDate.plusDays(1).atStartOfDay();

                    // [2] Raw Data 조회
                    List<SettlementSource> pendingData;

                    if(shopParam != null && !shopParam.isEmpty()){
                        UUID shopId = UUID.fromString(shopParam);
                        log.info("특정 판매자에 대한 정산 실행 - Shop: {}, Period: {} ~ {}", shopId, startDate, endDate);
                        pendingData = sourceRepository.findPendingByShopAndPeriod(shopId, startDateTime, endDateTimePlusOneDay);
                    }else{
                        log.info("전체 판매자에 대한 정산 실행 - Period: {} ~ {}", startDate, endDate);
                        pendingData = sourceRepository.findPendingByPeriod(startDateTime, endDateTimePlusOneDay);
                    }

                    if (pendingData.isEmpty()) {
                        log.info("정산할 데이터 없음");
                        return RepeatStatus.FINISHED;
                    }

                    // [3] 집계
                    List<SettlementSource> sourceList = new ArrayList<>();
                    List<SettlementResult> resultList = new ArrayList<>();

                    // 3-1. 판매자 ID별로 SettlementSource 데이터 그룹핑
                    Map<UUID, List<SettlementSource>> groupedByShop = pendingData.stream()
                            .collect(Collectors.groupingBy(SettlementSource::getShopId));
                    log.info("총 {}명의 판매자 정산 처리 중...", groupedByShop.size());

                    // 3-2. 판매자별 정산 실행
                    groupedByShop.forEach((shopId, shopDataList) -> {

                        // 매출액, 수수료, 정산금 계산
                        BigDecimal sales = shopDataList.stream()
                                .map(SettlementSource::getItemAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal fee = sales.multiply(FEE_RATE).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal payout = sales.subtract(fee);
                        LocalDateTime settledAt = LocalDateTime.now();

                        // SettlementSource 상태 변경 (PENDING -> COMPLETED)
                        shopDataList.forEach(s -> {
                            s.markCompleted();
                            sourceList.add(s);
                        });

                        // 정산 결과 생성
                        SettlementResult result = SettlementResult.create(
                                shopId,
                                startDate, endDate,
                                batchId, settledAt,
                                sales, FEE_RATE, fee, payout,
                                SettlementPayoutStatus.PENDING
                        );
                        resultList.add(result);
                    });

                    // [4] DB 저장
                    sourceRepository.saveAll(sourceList);
                    resultRepository.saveAll(resultList);

                    // [5] 정산금 지급
                    resultList.forEach(result -> {
                        try {
                            // TODO 에치금 정산 API 요청
                            // - 성공 시 result의 status PAID로 변경
                            //result.markPaid();
                        } catch(Exception e) {
                            // - 실패 시 result의 status FAILED로 변경, errorMsg 업데이트
                            //result.markFailed(e.getMessage());
                            //log.error("정산금 지급 실패 - Shop: {}", result.getShopId(), e);
                        }
                    });
                    resultRepository.saveAll(resultList);

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
