package com.node5.orderservice.global.exception.openfeign.client.dto;

import java.util.List;
import java.util.UUID;

public record StockHoldBatchResult(
        UUID orderId,
        List<StockReservationInfo> reservations
) {
}
