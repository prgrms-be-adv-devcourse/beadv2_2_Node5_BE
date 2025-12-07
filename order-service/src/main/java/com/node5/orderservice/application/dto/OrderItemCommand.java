package com.node5.orderservice.application.dto;

import java.util.UUID;

public record OrderItemCommand (
        UUID productId,
        String name,
        Integer unitPrice,
        Integer quantity,
        Integer totalPrice
) {
}
