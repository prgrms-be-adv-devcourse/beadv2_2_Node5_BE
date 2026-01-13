package com.node5.catalogservice.inventory.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.node5.catalogservice.inventory.domain.StockReservation;

public interface StockReservationJpaRepository extends JpaRepository<StockReservation, UUID> {
	Optional<StockReservation> findByOrderIdAndProductId(UUID orderId, UUID productId);
}
