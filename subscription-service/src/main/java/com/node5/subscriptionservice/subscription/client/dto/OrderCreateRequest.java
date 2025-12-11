package com.node5.subscriptionservice.subscription.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record OrderCreateRequest(
        UUID memberId,
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
