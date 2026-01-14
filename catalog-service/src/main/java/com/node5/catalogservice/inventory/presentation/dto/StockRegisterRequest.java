package com.node5.catalogservice.inventory.presentation.dto;

import java.util.UUID;

import com.node5.catalogservice.inventory.application.dto.StockRegisterCommand;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockRegisterRequest(
	@NotNull
	UUID productId,

	@Min(0)
	int quantity
) {

	public StockRegisterCommand toCommand() {
		return new StockRegisterCommand(productId, quantity);
	}
}
