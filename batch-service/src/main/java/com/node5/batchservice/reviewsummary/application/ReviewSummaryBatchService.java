package com.node5.batchservice.reviewsummary.application;


import com.node5.batchservice.reviewsummary.application.dto.JobExecutionResponse;
import com.node5.batchservice.reviewsummary.application.dto.ReviewSummaryExecutionInfoResponse;
import com.node5.batchservice.reviewsummary.application.dto.ReviewSummaryExecutionListResponse;
import com.node5.batchservice.reviewsummary.exception.ReviewSummaryBatchErrorCode;
import com.node5.batchservice.reviewsummary.exception.ReviewSummaryBatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewSummaryBatchService {

    private final Job monthlyReviewSummaryJob;
    private final JobLauncher jobLauncher;

    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;

    public JobExecutionResponse runReviewSummary() {
        if (isJobRunning(monthlyReviewSummaryJob.getName())) {
            throw new ReviewSummaryBatchException(ReviewSummaryBatchErrorCode.JOB_IS_RUNNING);
        }

        LocalDate batchStartDate = LocalDate.now();

        JobParameters params = new JobParametersBuilder()
                .addString("batchStartDate", batchStartDate.toString())
                .addLong("runAt", System.currentTimeMillis())
                .toJobParameters();

        try {
            JobExecution execution = jobLauncher.run(monthlyReviewSummaryJob, params);
            return new JobExecutionResponse(execution.getId());
        } catch (JobExecutionAlreadyRunningException e) {
            throw new ReviewSummaryBatchException(ReviewSummaryBatchErrorCode.JOB_IS_RUNNING);
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new ReviewSummaryBatchException(ReviewSummaryBatchErrorCode.JOB_ALREADY_COMPLETED);
        } catch (JobParametersInvalidException | JobRestartException e) {
            throw new ReviewSummaryBatchException(ReviewSummaryBatchErrorCode.JOB_LAUNCH_FAILED);
        }
    }

    private boolean isJobRunning(String jobName) {
        return !jobExplorer.findRunningJobExecutions(jobName).isEmpty();
    }

    @Transactional(readOnly = true)
    public List<ReviewSummaryExecutionListResponse> getExecutions(Pageable pageable) {

        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();

        List<JobInstance> instances = jobExplorer.getJobInstances(monthlyReviewSummaryJob.getName(), 0, pageSize * (pageNumber + 1));

        List<JobExecution> executions = instances.stream()
                .flatMap(instance -> jobExplorer.getJobExecutions(instance).stream())
                .sorted(Comparator.comparing(JobExecution::getCreateTime).reversed())
                .toList();

        int fromIndex = Math.min(executions.size(), pageSize * pageNumber);
        int toIndex = Math.min(executions.size(), fromIndex + pageSize);

        return executions.subList(fromIndex, toIndex).stream()
                .map(ReviewSummaryExecutionListResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReviewSummaryExecutionInfoResponse getExecutionInfo(Long executionId) {
        JobExecution execution = jobExplorer.getJobExecution(executionId);
        if(execution == null) {
            throw new ReviewSummaryBatchException(ReviewSummaryBatchErrorCode.JOB_EXECUTION_NOT_FOUND);
        }

        return ReviewSummaryExecutionInfoResponse.from(execution);
    }

    public JobExecutionResponse restartExecution(Long executionId) {
        if (isJobRunning(monthlyReviewSummaryJob.getName())) {
            throw new ReviewSummaryBatchException(ReviewSummaryBatchErrorCode.JOB_IS_RUNNING);
        }
        JobExecution execution = jobExplorer.getJobExecution(executionId);
        if (execution == null) {
            throw new ReviewSummaryBatchException(ReviewSummaryBatchErrorCode.JOB_EXECUTION_NOT_FOUND);
        }

        if (!execution.getStatus().isUnsuccessful()) {
            throw new ReviewSummaryBatchException(ReviewSummaryBatchErrorCode.JOB_NOT_RESTARTABLE);
        }

        try {
            Long newExecutionId = jobOperator.restart(executionId);
            return new JobExecutionResponse(newExecutionId);
        } catch (Exception e) {
            throw new ReviewSummaryBatchException(ReviewSummaryBatchErrorCode.JOB_RESTART_FAILED);
        }
    }
}
