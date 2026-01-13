package com.node5.catalogservice.inventory.application.dto;

import java.util.UUID;

import com.node5.catalogservice.inventory.domain.StockReservation;
import com.node5.catalogservice.inventory.domain.ReservationStatus;

public record StockReservationInfo(
	UUID reservationId,
	UUID orderId,
	UUID productId,
	int quantity,
	ReservationStatus status
) {
	public static StockReservationInfo from(StockReservation r) {
		return new StockReservationInfo(
			r.getId(),
			r.getOrderId(),
			r.getProductId(),
			r.getQuantity(),
			r.getStatus());
	}
}
