package com.node5.supportservice.reviewsummary.application.dto;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewSummaryExecutionInfoResponse(
        Long executionId,
        BatchStatus status,
        ExitStatus exitStatus,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<String> failureExceptions,
        List<StepExecutionResponse> steps
) {
    public static ReviewSummaryExecutionInfoResponse from(JobExecution execution) {
        return new ReviewSummaryExecutionInfoResponse(
                execution.getId(),
                execution.getStatus(),
                execution.getExitStatus(),
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
