package com.node5.orderservice.application.dto;

import com.node5.orderservice.domain.OrderType;

import java.util.List;
import java.util.UUID;

public record OrderCommand(
        UUID memberId,
        OrderType orderType,
        UUID subscriptionId,
        String recipientName,
        String recipientAddress,
        List<OrderItemCommand> items
) {
}
