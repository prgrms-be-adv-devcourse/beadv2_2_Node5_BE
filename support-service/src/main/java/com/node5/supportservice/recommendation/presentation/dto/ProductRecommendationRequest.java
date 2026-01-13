package com.node5.supportservice.recommendation.presentation.dto;

import java.util.List;
import java.util.UUID;

public record ProductRecommendationRequest(
        float[] preferenceEmbedding,
        List<UUID> excludedProductIds,
        Integer limit
) {
}
