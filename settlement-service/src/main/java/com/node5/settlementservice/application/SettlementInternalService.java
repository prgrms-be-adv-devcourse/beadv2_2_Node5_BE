package com.node5.settlementservice.application;

import com.node5.settlementservice.application.dto.JobExecutionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class SettlementInternalService {

    private final JobExplorer jobExplorer;

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
}
