package com.node5.settlementservice.application;

import com.node5.common.domain.PageInfoDto;
import com.node5.settlementservice.application.dto.JobExecutionInfo;
import com.node5.settlementservice.application.dto.SettlementListInfo;
import com.node5.settlementservice.domain.SettlementResult;
import com.node5.settlementservice.domain.SettlementResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final JobExplorer jobExplorer;
    private final SettlementResultRepository settlementResultRepository;

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

        return new JobExecutionInfo(
                execution.getId(),
                execution.getStatus().toString(),
                start,
                execution.getEndTime(),
                duration,
                exitDescription,
                execution.getJobInstance().getJobName(),
                shopId == null ? "전체" : shopId,
                period
        );
    }

    public SettlementListInfo getSettlementHistory(UUID shopId, YearMonth startYm, YearMonth endYm, int page) {
        LocalDate periodStart = startYm.atDay(1);
        LocalDate periodEnd = endYm.atEndOfMonth();

        Pageable pageable = PageRequest.of(page, 12);
        Page<SettlementResult> result = settlementResultRepository.findByShopIdAndTargetStartDateBetweenOrderByTargetEndDateDesc(shopId, periodStart, periodEnd, pageable);
        PageInfoDto pageInfo = new PageInfoDto(result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());

        if(!result.hasContent()){
            return SettlementListInfo.from(pageInfo, Collections.emptyList());
        }

        List<SettlementListInfo.SettlementListDetailInfo> detailInfoList = result.getContent().stream()
                .map(SettlementListInfo.SettlementListDetailInfo::from)
                .toList();

        return SettlementListInfo.from(pageInfo, detailInfoList);
    }
}
