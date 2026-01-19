package com.node5.orderservice.order.client.dto;

import java.util.List;
import java.util.UUID;

public record StockReleaseBatchRequest(
        UUID orderId,
        List<StockReleaseItemRequest> items
) {
    public record StockReleaseItemRequest(
            UUID productId
    ) { }
}
