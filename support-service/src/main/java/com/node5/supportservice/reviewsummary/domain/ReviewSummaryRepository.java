package com.node5.supportservice.reviewsummary.domain;

import java.util.Optional;
import java.util.UUID;

public interface ReviewSummaryRepository {
    Optional<ReviewSummary> findByProductId(UUID productId);
    void save(ReviewSummary reviewSummary);
}
