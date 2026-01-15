package com.node5.supportservice.reviewsummary.application.dto;

import com.node5.supportservice.reviewsummary.domain.ReviewSummary;

import java.time.LocalDateTime;

public record ReviewSummaryInfoResponse(
        LocalDateTime summarizedAt,
        String summary
) {
    public static ReviewSummaryInfoResponse empty() {
        return new ReviewSummaryInfoResponse(null, null);
    }

    public static ReviewSummaryInfoResponse from(ReviewSummary reviewSummary) {
        return new ReviewSummaryInfoResponse(reviewSummary.getModifiedAt(), reviewSummary.getSummary());
    }

}
