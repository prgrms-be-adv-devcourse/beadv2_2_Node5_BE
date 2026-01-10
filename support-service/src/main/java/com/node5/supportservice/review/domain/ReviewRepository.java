package com.node5.supportservice.review.domain;

import java.util.UUID;

public interface ReviewRepository {
    Review findByProductId(UUID productId);

    Review save(Review review);

    void incrementStatistics(UUID productId, Integer rating);

    void decrementStatistics(UUID productId, Integer rating);

    Boolean existsByProductId(UUID productId);

    void updateStatisticsOnReviewEdit(UUID productId, Integer oldRating, Integer newRating);
}
