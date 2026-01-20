package com.node5.orderservice.subscription.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductInfoResponse(
        UUID id,
        UUID shopId,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String status,
        String category,
        String thumbnailUrl,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {
}
