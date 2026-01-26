package com.node5.batchservice.subscription.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SubscriptionBatchTarget(
        UUID subscriptionId,
        UUID memberId,
        String deliveryAddress,
        UUID productId,
        String productName,
        String thumbnailKey,
        BigDecimal pricePerItem,
        Integer quantity,
        BigDecimal totalPrice
) {
}
