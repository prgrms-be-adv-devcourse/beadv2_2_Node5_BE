package com.node5.orderservice.global.openfeign.client.dto;

import java.util.UUID;

public record StockReservationInfo(
        UUID reservationId,
        UUID orderId,
        UUID productId,
        int quantity,
        ReservationStatus status
) {
    public enum ReservationStatus {
        HELD,
        COMMITTED,
        RELEASED
    }
}