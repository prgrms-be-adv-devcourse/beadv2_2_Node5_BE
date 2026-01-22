package com.node5.supportservice.batch.application.dto;

import com.node5.supportservice.batch.domain.BatchExecutionRow;
import com.node5.supportservice.batch.domain.BatchStepExecutionRow;

import java.time.LocalDateTime;
import java.util.List;

public record BatchExecutionWithStepsResponse(
        Long executionId,
        String jobName,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String exitCode,

        List<BatchStepExecutionRow> steps
) {
    public static BatchExecutionWithStepsResponse from(BatchExecutionRow execution, List<BatchStepExecutionRow> steps) {
        return new BatchExecutionWithStepsResponse(
                execution.executionId(),
                execution.jobName(),
                execution.status(),
                execution.startTime(),
                execution.endTime(),
                execution.exitCode(),
                steps
        );
    }
}
