package com.node5.orderservice.order.infrastructure.kafka;

import java.util.List;
import java.util.UUID;

public record StockRestoreKafkaRequest(
        UUID cancelEventId,
        UUID orderId,
        List<StockRestoreEvent.StockRestoreItemCommand> items
) {
    public static StockRestoreKafkaRequest create(StockRestoreEvent event) {
        return new StockRestoreKafkaRequest(event.cancelEventId(), event.orderId(), event.items());
    }
}
