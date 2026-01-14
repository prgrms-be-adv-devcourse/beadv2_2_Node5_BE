package com.node5.catalogservice.inventory.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.node5.catalogservice.inventory.domain.Stock;
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

	@Override
	public int increase(UUID productId, int quantity) {
		return stockJpaRepository.increase(productId, quantity);
	}

	@Override
	public Stock save(Stock stock) {
		return stockJpaRepository.save(stock);
	}

	@Override
	public Optional<Stock> findById(UUID productId) {
		return stockJpaRepository.findById(productId);
	}
}
