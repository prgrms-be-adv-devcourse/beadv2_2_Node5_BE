package com.node5.batchservice.reviewsummary.application.dto;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewSummaryExecutionInfoResponse(
        Long executionId,
        BatchStatus batchStatus,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<String> failureExceptions,
        List<StepExecutionResponse> steps
) {
    public static ReviewSummaryExecutionInfoResponse from(JobExecution execution) {
        return new ReviewSummaryExecutionInfoResponse(
                execution.getId(),
                execution.getStatus(),
                execution.getStartTime(),
                execution.getEndTime(),
                execution.getAllFailureExceptions()
                        .stream()
                        .map(Throwable::getMessage)
                        .toList(),
                execution.getStepExecutions().stream()
                        .map(StepExecutionResponse::from)
                        .toList()
        );
    }
}
