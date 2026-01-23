package com.node5.supportservice.batch.domain;

import java.time.LocalDateTime;

public record BatchExecutionRow(
        Long executionId,
        String jobName,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String exitCode
) {
}
