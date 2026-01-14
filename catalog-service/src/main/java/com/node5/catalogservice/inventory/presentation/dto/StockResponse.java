package com.node5.catalogservice.inventory.presentation.dto;

import java.util.UUID;

import com.node5.catalogservice.inventory.domain.Stock;

public record StockResponse(
	UUID productId,
	int quantity
) {
	public static StockResponse from(Stock stock) {
		return new StockResponse(stock.getProductId(), stock.getQuantity());
	}
}
