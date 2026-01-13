package com.node5.catalogservice.inventory.domain;

import java.util.Optional;
import java.util.UUID;

public interface StockReservationRepository {
	Optional<StockReservation> findByOrderIdAndProductId(UUID orderId, UUID productId);

	StockReservation save(StockReservation reservation);

	int updateStatus(UUID orderId, UUID productId, ReservationStatus fromStatus, ReservationStatus toStatus);
}
