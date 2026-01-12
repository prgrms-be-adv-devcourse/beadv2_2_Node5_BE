package com.node5.supportservice.review.application.dto;

import com.node5.supportservice.review.domain.ReviewStatic;

import java.math.BigDecimal;
import java.util.UUID;

public record ReviewInfo(
        UUID id,
        UUID productId,
        BigDecimal averageRating,
        Integer reviewCount,
        Integer ratingCount1,
        Integer ratingCount2,
        Integer ratingCount3,
        Integer ratingCount4,
        Integer ratingCount5
) {
    public static ReviewInfo from(ReviewStatic review, BigDecimal averageRating) {
        return new ReviewInfo(
                review.getId(),
                review.getProductId(),
                averageRating,
                review.getReviewCount(),
                review.getRatingCount1(),
                review.getRatingCount2(),
                review.getRatingCount3(),
                review.getRatingCount4(),
                review.getRatingCount5()
        );
    }
}
