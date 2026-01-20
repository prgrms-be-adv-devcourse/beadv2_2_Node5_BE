package com.node5.batchservice.reviewsummary.client.dto;

import java.time.LocalDateTime;

public record ReviewSummaryInfoResponse(
        LocalDateTime summarizedAt,
        String summary
) {
}
