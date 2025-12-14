package com.node5.orderservice.order.application.dto;

import com.node5.orderservice.order.domain.Order;

import java.util.UUID;

public record OrderCreateInfo(
        UUID orderId
) {

    public static OrderCreateInfo from(Order order) {
        return new OrderCreateInfo(
                order.getId()
        );
    }
}
