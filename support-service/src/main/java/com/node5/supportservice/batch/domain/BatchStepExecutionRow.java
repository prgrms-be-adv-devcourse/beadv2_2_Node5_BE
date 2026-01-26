package com.node5.supportservice.batch.domain;

public record BatchStepExecutionRow(
        String stepName,
        String status,
        int readCount,
        int writeCount,
        int filterCount,
        int commitCount,
        int rollbackCount,
        String exitMessage
) {
}
