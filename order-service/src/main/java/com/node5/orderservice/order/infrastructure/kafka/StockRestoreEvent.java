package com.node5.orderservice.order.infrastructure.kafka;

import com.node5.orderservice.order.domain.OrderItem;

import java.util.UUID;
import java.util.List;

public record StockRestoreEvent(
        UUID cancelEventId,
        UUID orderId,
        List<StockRestoreItemCommand> items,
        String type
) {
    public record StockRestoreItemCommand(
            UUID productId,
            int quantity
    ) { }

    public static StockRestoreEvent create(UUID orderId, List<OrderItem> orderItems, String type){
        return new StockRestoreEvent(
                UUID.randomUUID(),
                orderId,
                orderItems.stream()
                        .map(oi -> new StockRestoreItemCommand(oi.getProductId(), oi.getQuantity()))
                        .toList(),
                type
        );
    }
}
