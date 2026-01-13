package com.node5.supportservice.review.application.dto;

import java.util.UUID;

public record ReviewIdInfo(
        UUID reviewId
) {
    public static ReviewIdInfo from(UUID reviewId) {
        return new ReviewIdInfo(
                reviewId
        );
    }
}
