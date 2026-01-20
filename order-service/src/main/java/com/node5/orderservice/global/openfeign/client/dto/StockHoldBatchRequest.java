package com.node5.orderservice.global.openfeign.client.dto;

import java.util.List;
import java.util.UUID;

public record StockHoldBatchRequest(
        UUID orderId,
        List<StockHoldItemRequest> items
) {
    public record StockHoldItemRequest(
            UUID productId,
            Integer quantity
    ) { }
}
