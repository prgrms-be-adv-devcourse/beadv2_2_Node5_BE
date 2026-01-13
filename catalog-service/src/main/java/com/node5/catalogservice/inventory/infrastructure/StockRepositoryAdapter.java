package com.node5.catalogservice.inventory.infrastructure;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.node5.catalogservice.inventory.domain.StockRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StockRepositoryAdapter implements StockRepository {

	private final StockJpaRepository stockJpaRepository;

	@Override
	public boolean existsById(UUID productId) {
		return stockJpaRepository.existsByProductId(productId);
	}

	@Override
	public boolean decreaseIfEnough(UUID productId, int quantity) {
		return stockJpaRepository.decreaseIfEnough(productId, quantity) == 1;
	}
}
