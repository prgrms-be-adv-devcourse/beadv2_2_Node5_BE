package com.node5.batchservice.reviewsummary.client.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ReviewSummaryUpsertRequest(
        UUID productId,
        String summary,
        LocalDate endDate
) {
}
