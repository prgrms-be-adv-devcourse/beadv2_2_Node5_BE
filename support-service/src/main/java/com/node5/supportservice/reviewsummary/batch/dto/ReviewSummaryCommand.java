package com.node5.supportservice.reviewsummary.batch.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ReviewSummaryCommand(
        UUID productId,
        String summary,
        LocalDate startDate,
        LocalDate endDate
) {
}
