package com.node5.catalogservice.inventory.presentation.dto;

import java.util.UUID;

import com.node5.catalogservice.inventory.application.dto.StockReleaseCommand;

import jakarta.validation.constraints.NotNull;

public record StockReleaseRequest(
	@NotNull
	UUID orderId,

	@NotNull
	UUID productId
) {

	public StockReleaseCommand toCommand() {
		return new StockReleaseCommand(orderId, productId);
	}
}
