package com.node5.supportservice.global.openfeign.client.dto;

import java.util.List;
import java.util.UUID;

public record ProductSummaryListResponse(
        List<ProductSummaryResponse> products
) {
    public record ProductSummaryResponse(
            UUID productId,
            String name,
            String category,
            String description
    ) { }
}