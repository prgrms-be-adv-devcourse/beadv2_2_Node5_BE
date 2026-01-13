package com.node5.catalogservice.inventory.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.node5.catalogservice.inventory.domain.StockReservation;
import com.node5.catalogservice.inventory.domain.StockReservationRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StockReservationRepositoryAdapter implements StockReservationRepository {

	private final StockReservationJpaRepository stockReservationJpaRepository;

	@Override
	public Optional<StockReservation> findByOrderIdAndProductId(UUID orderId, UUID productId) {
		return stockReservationJpaRepository.findByOrderIdAndProductId(orderId, productId);
	}

	@Override
	public StockReservation save(StockReservation reservation) {
		return stockReservationJpaRepository.save(reservation);
	}
}
