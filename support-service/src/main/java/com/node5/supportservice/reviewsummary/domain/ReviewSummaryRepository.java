package com.node5.supportservice.reviewsummary.domain;

import java.util.List;
import java.util.UUID;

public interface ReviewSummaryRepository {
    List<ReviewSummary> findByProductIdOrderByRatingDesc(UUID productId);
}
