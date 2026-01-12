package com.node5.supportservice.review.domain;

import java.util.UUID;

public interface ReviewRepository {
    ReviewStatic findByProductId(UUID productId);

    ReviewStatic save(ReviewStatic review);

    void incrementStatistics(UUID productId, Integer rating);

    void decrementStatistics(UUID productId, Integer rating);

    Boolean existsByProductId(UUID productId);

    void updateStatisticsOnReviewEdit(UUID productId, Integer oldRating, Integer newRating);
}
