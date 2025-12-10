package com.node5.orderservice.application.dto;

import com.node5.orderservice.domain.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemInfo(
        UUID productId,
        String productName,
        String imgUrl,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal totalPrice
) {

    public static OrderItemInfo from(OrderItem oi) {
        return new OrderItemInfo(
                oi.getProductId(),
                oi.getName(),
                oi.getImgUrl(),
                oi.getUnitPrice(),
                oi.getQuantity(),
                oi.getTotalPrice()
        );
    }
}

