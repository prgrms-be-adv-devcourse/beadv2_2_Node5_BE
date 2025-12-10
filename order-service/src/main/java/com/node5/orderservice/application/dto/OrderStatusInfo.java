package com.node5.orderservice.application.dto;

import com.node5.orderservice.domain.Order;
import com.node5.orderservice.domain.OrderStatus;

import java.util.UUID;

public record OrderStatusInfo(
        UUID orderId,
        OrderStatus status
) {

    public static OrderStatusInfo from(Order order){
        return new OrderStatusInfo(order.getId(), order.getStatus());
    }
}
