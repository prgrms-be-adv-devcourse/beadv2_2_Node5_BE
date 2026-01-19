package com.node5.supportservice.reviewsummary.application.dto;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;

import java.util.List;

public record StepExecutionResponse(
        String stepName,
        BatchStatus batchStatus,
        ExitStatus exitStatus,
        long readCount,
        long writeCount,
        long skipCount,
        List<String> failureExceptions
) {
    public static StepExecutionResponse from(StepExecution step) {
        return new StepExecutionResponse(
                step.getStepName(),
                step.getStatus(),
                step.getExitStatus(),
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
