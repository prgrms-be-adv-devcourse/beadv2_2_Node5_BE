package com.node5.common.event;

import java.util.List;
import java.util.UUID;

public record ProductSalesIncrementEvent(
        UUID productSalesIncrementEventId,
        List<Item> items
) {
    public record Item(
            UUID orderId,
            UUID productId,
            int quantity
    ){ }
}
