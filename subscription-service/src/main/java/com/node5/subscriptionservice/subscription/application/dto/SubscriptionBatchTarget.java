package com.node5.subscriptionservice.subscription.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SubscriptionBatchTarget(
        UUID subscriptionId,
        UUID memberId,
        String deliveryAddress,
        UUID productId,
        String productName,
        String thumbnailUrl,
        BigDecimal pricePerItem,
        Integer quantity,
        BigDecimal totalPrice
) {
}
