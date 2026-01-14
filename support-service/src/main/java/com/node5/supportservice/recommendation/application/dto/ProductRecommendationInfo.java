package com.node5.supportservice.recommendation.application.dto;

import java.util.List;
import java.util.UUID;

public record ProductRecommendationInfo(
        List<UUID> productIds
) {
    public static ProductRecommendationInfo of(List<UUID> productId) {
        return new ProductRecommendationInfo(productId);
    }
}
