package com.node5.orderservice.order.application.dto;

import com.node5.orderservice.order.domain.OrderType;

import java.util.List;
import java.util.UUID;

public record OrderCommand(
        OrderType orderType,
        UUID subscriptionKey,
        String recipientName,
        String recipientAddress,
        List<OrderItemCommand> items
) {
}
