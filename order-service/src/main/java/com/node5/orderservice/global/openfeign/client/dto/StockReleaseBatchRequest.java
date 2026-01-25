package com.node5.orderservice.global.openfeign.client.dto;

import com.node5.orderservice.order.domain.OrderItem;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record StockReleaseBatchRequest(
        UUID orderId,
        List<StockReleaseItemRequest> items
) {
    public record StockReleaseItemRequest(
            UUID productId
    ) {
    }

    public static StockReleaseBatchRequest create(UUID orderId, List<OrderItem> items){
        return new StockReleaseBatchRequest(
                orderId,
                items.stream()
                        .map(item -> new StockReleaseItemRequest(item.getProductId()))
                        .collect(Collectors.toList())
        );
    }
}
