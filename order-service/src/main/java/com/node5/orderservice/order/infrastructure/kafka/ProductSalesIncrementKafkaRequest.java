package com.node5.orderservice.order.infrastructure.kafka;

import com.node5.common.event.ProductSalesIncrementEvent;
import com.node5.orderservice.order.domain.OrderItem;

import java.util.List;
import java.util.UUID;

public record ProductSalesIncrementKafkaRequest(
        UUID productSalesIncrementEventId,
        List<ProductSalesInfo> items
) {
    public record ProductSalesInfo(
            UUID orderId,
            UUID productId,
            int quantity
    ){ }

    public static ProductSalesIncrementKafkaRequest create(List<OrderItem> orderItems){
        return new ProductSalesIncrementKafkaRequest(
                UUID.randomUUID(),
                orderItems.stream()
                        .map(oi -> new ProductSalesInfo(oi.getOrderId(), oi.getProductId(), oi.getQuantity()))
                        .toList()
        );
    }

    public ProductSalesIncrementEvent toEvent() {
        List<ProductSalesIncrementEvent.Item> eventItems = this.items.stream()
                .map(item -> new ProductSalesIncrementEvent.Item(item.orderId(), item.productId(), item.quantity()))
                .toList();

        return new ProductSalesIncrementEvent(
                this.productSalesIncrementEventId,
                eventItems
        );
    }

}
