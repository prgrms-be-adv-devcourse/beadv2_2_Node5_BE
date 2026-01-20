package com.node5.orderservice.order.application.dto;

import java.util.UUID;

public record OrderStatusCommand(
        UUID orderId,
        UUID productId
) {
}
