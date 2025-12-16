package com.node5.subscriptionservice.subscription.client.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderCreateRequest(
        String orderType,
        UUID subscriptionId,
        String recipientName,
        String recipientAddress,
        List<OrderItemRequest> items
) {
    public record OrderItemRequest(
            UUID productId,
            String name,
            String imgUrl,
            BigDecimal unitPrice,
            Integer quantity,
            BigDecimal totalPrice
    ){}
}
