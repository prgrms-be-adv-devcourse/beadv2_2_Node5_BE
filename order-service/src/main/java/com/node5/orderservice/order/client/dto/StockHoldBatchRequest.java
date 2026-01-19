package com.node5.orderservice.order.client.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
