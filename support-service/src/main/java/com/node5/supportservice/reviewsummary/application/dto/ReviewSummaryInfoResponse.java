package com.node5.supportservice.reviewsummary.application.dto;

import com.node5.supportservice.reviewsummary.domain.ReviewSummary;

import java.time.LocalDateTime;

public record ReviewSummaryInfoResponse(
        int rating,
        LocalDateTime summarizedAt,
        String summary
) {
    public static ReviewSummaryInfoResponse from(ReviewSummary reviewSummary) {
        return new ReviewSummaryInfoResponse(reviewSummary.getRating(), reviewSummary.getModifiedAt(), reviewSummary.getSummary());
    }

}
