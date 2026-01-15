package com.node5.catalogservice.inventory.presentation.dto;

import java.util.UUID;

import com.node5.catalogservice.inventory.application.dto.StockHoldCommand;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockHoldRequest(
	@NotNull
	UUID orderId,

	@NotNull
	UUID productId,

	@Min(1)
	int quantity
) {

	public StockHoldCommand toCommand() {
		return new StockHoldCommand(orderId, productId, quantity);
	}
}
