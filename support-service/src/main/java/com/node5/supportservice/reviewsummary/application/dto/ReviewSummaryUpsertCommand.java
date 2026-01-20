package com.node5.supportservice.reviewsummary.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ReviewSummaryUpsertCommand(
        UUID productId,
        String summary,
        LocalDate endDate
) {
}
