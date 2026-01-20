package com.node5.orderservice.global.exception.openfeign.client.dto;

import java.util.List;
import java.util.UUID;

public record StockCommitBatchRequest(
        UUID orderId,
        List<StockCommitItemRequest> items
) {
    public record StockCommitItemRequest(
            UUID productId
    ) { }
}
