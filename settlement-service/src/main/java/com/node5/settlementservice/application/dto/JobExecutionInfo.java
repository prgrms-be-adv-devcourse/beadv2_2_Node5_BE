package com.node5.settlementservice.application.dto;

import java.time.LocalDateTime;

public record JobExecutionInfo (
        Long jobExecutionId,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        long durationMillis,
        String exitMessage,
        String jobName,
        String shopId,
        String period
) {
}
