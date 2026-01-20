package com.node5.batchservice.reviewsummary.application.dto;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.StepExecution;

import java.util.List;

public record StepExecutionResponse(
        String stepName,
        BatchStatus batchStatus,
        long readCount,
        long writeCount,
        long skipCount,
        List<String> failureExceptions
) {
    public static StepExecutionResponse from(StepExecution step) {
        return new StepExecutionResponse(
                step.getStepName(),
                step.getStatus(),
                step.getReadCount(),
                step.getWriteCount(),
                step.getSkipCount(),
                step.getFailureExceptions()
                        .stream()
                        .map(Throwable::getMessage)
                        .toList()
        );
    }
}
