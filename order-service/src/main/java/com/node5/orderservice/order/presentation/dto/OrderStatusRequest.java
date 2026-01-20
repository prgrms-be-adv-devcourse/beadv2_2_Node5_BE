package com.node5.orderservice.order.presentation.dto;

import com.node5.orderservice.order.application.dto.OrderStatusCommand;

import java.util.UUID;

public record OrderStatusRequest(
    UUID orderId,
    UUID productId
) {
    public OrderStatusCommand toCommand(){
        return new OrderStatusCommand(orderId, productId);
    }
}
