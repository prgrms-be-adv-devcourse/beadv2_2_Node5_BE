package com.node5.catalogservice.inventory.domain;

import java.util.Optional;
import java.util.UUID;

public interface StockRepository {
	boolean existsById(UUID productId);

	/**
	 * 조건부 차감: 성공이면 true(= row 1), 실패면 false(= row 0)
	 */
	boolean decreaseIfEnough(UUID productId, int quantity);
}
