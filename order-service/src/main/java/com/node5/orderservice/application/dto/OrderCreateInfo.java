package com.node5.orderservice.application.dto;

import com.node5.orderservice.domain.Order;

import java.util.UUID;

public record OrderCreateInfo(
        UUID orderId
) {

    public static OrderCreateInfo from (Order order) {
        return new OrderCreateInfo(
                order.getId()
        );
    }
}
