package com.node5.supportservice.reviewsummary.presentation.dto;

import com.node5.supportservice.reviewsummary.application.dto.ReviewSummaryUpsertCommand;

import java.time.LocalDate;
import java.util.UUID;

public record ReviewSummaryUpsertRequest(
        UUID productId,
        String summary,
        LocalDate endDate
) {
    public ReviewSummaryUpsertCommand toCommand() {
        return new ReviewSummaryUpsertCommand(productId, summary, endDate);
    }
}
