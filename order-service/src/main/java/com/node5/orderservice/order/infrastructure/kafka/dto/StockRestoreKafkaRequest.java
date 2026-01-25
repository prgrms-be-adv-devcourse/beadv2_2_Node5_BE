package com.node5.orderservice.order.infrastructure.kafka.dto;

import com.node5.common.event.StockRestoreEvent;
import com.node5.orderservice.order.domain.OrderItem;

import java.util.List;
import java.util.UUID;

public record StockRestoreKafkaRequest(
        UUID cancelEventId,
        UUID orderId,
        List<Item> items,
        String type
) {
    public record Item(
            UUID productId,
            int quantity
    ) { }

    public static StockRestoreKafkaRequest create(UUID orderId, List<OrderItem> orderItems, String type){
        return new StockRestoreKafkaRequest(
                UUID.randomUUID(),
                orderId,
                orderItems.stream()
                        .map(oi -> new Item(oi.getProductId(), oi.getQuantity()))
                        .toList(),
                type
        );
    }

    public StockRestoreEvent toEvent() {
        List<StockRestoreEvent.Item> eventItems = this.items.stream()
                .map(item -> new StockRestoreEvent.Item(item.productId(), item.quantity()))
                .toList();

        return new StockRestoreEvent(
                this.cancelEventId,
                this.orderId,
                eventItems
        );
    }
}
