package com.node5.batchservice.settlement.application;

import com.node5.batchservice.settlement.application.dto.JobExecutionInfo;
import com.node5.batchservice.settlement.exception.SettlementBatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.node5.batchservice.settlement.exception.SettlementBatchErrorCode.BATCH_JOB_LAUNCH_FAILED;
import static com.node5.batchservice.settlement.exception.SettlementBatchErrorCode.SETTLEMENT_ALREADY_COMPLETED;

@Service
@RequiredArgsConstructor
public class SettlementBatchService {

    private final JobLauncher jobLauncher;
    private final Job shopSettlementJob;
    private final JobExplorer jobExplorer;

    // 전체 판매자 월별 정산 배치 실행
    public Long runAll(String yearMonth) {
        JobParameters params = getDefaultSettlementParamsBuilder(yearMonth)
                .toJobParameters();

        try {
            return jobLauncher.run(shopSettlementJob, params).getId();
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new SettlementBatchException(SETTLEMENT_ALREADY_COMPLETED);
        } catch (Exception e) {
            throw new SettlementBatchException(BATCH_JOB_LAUNCH_FAILED, e.getMessage());
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
            throw new SettlementBatchException(SETTLEMENT_ALREADY_COMPLETED);
        } catch (Exception e) {
            throw new SettlementBatchException(BATCH_JOB_LAUNCH_FAILED, e.getMessage());
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

}
