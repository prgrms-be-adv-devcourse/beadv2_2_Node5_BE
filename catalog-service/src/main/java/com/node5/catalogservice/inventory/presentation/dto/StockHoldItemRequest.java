package com.node5.catalogservice.inventory.presentation.dto;

import java.util.UUID;

import com.node5.catalogservice.inventory.application.dto.StockHoldBatchCommand;

import jakarta.validation.constraints.NotNull;

public record StockHoldItemRequest(
	@NotNull
	UUID productId,

	@NotNull
	Integer quantity
) {
	public StockHoldBatchCommand.Item toCommand() {
		return new StockHoldBatchCommand.Item(productId, quantity);
	}
}
