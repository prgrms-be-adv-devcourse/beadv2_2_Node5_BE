package com.node5.batchservice.reviewsummary.application.dto;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;

import java.time.LocalDateTime;

public record ReviewSummaryExecutionListResponse(
        Long executionId,
        BatchStatus batchStatus,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
    public static ReviewSummaryExecutionListResponse from(JobExecution execution) {
        return new ReviewSummaryExecutionListResponse(
                execution.getId(),
                execution.getStatus(),
                execution.getStartTime(),
                execution.getEndTime()
        );
    }
}
