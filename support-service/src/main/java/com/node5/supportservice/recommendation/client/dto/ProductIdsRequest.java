package com.node5.supportservice.recommendation.client.dto;

import java.util.List;
import java.util.UUID;

public record ProductIdsRequest(
        List<UUID> productIds
) {
    public static ProductIdsRequest from(List<UUID> productIds) {
        return new ProductIdsRequest(productIds);
    }
}
