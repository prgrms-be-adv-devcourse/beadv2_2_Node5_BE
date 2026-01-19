package com.node5.catalogservice.inventory.application.dto;

import java.util.List;
import java.util.UUID;

public record StockHoldBatchResult(
	UUID orderId,
	List<StockReservationInfo> reservations
) {
	public static StockHoldBatchResult of(UUID orderId, List<StockReservationInfo> reservations) {
		return new StockHoldBatchResult(orderId, reservations);
	}
}
