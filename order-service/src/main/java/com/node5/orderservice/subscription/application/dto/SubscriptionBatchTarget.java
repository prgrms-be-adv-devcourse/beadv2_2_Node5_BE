package com.node5.orderservice.subscription.application.dto;

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
