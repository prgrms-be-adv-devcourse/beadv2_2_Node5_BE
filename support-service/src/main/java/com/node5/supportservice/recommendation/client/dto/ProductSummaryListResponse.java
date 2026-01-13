package com.node5.supportservice.recommendation.client.dto;

import java.util.List;

public record ProductSummaryListResponse(
     List<ProductSummaryResponse> products
) {
    public record ProductSummaryResponse(
            Long productId,
            String name,
            String category,
            String description
    ) { }
}
