package com.node5.settlementservice.settlement.application.dto;

import org.springframework.batch.core.JobExecution;

import java.time.LocalDateTime;

public record JobExecutionInfo (
        Long jobExecutionId,
        String shopId,
        String period,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        long durationMillis,
        String exitMessage
) {

    public static JobExecutionInfo from(
            JobExecution execution,
            String shopId,
            String period,
            LocalDateTime start,
            LocalDateTime end,
            long duration,
            String exitDescription
    ) {
        return new JobExecutionInfo(
                execution.getId(),
                shopId == null ? "전체" : shopId,
                period,
                execution.getStatus().toString(),
                start,
                end,
                duration,
                exitDescription
        );
    }
}
