package com.node5.supportservice.recommendation.presentation.dto;

import java.util.List;
import java.util.UUID;

public record ProductRecommendationRequest(
        List<UUID> cartItemIds,
        Integer limit
) {
}
