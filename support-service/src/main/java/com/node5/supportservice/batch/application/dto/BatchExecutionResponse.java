package com.node5.supportservice.batch.application.dto;

import com.node5.supportservice.batch.domain.BatchExecutionRow;

import java.time.LocalDateTime;

public record BatchExecutionResponse(
        Long executionId,
        String jobName,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String exitCode
) {
    public static BatchExecutionResponse from(BatchExecutionRow execution) {
        return new BatchExecutionResponse(
                execution.executionId(),
                execution.jobName(),
                execution.status(),
                execution.startTime(),
                execution.endTime(),
                execution.exitCode()
        );
    }
}
