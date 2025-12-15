package com.node5.orderservice.order.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemCommand(
        UUID productId,
        String name,
        String imgUrl,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal totalPrice
) {
}
