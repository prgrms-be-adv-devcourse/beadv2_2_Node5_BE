package com.node5.catalogservice.inventory.presentation.dto;

import java.util.UUID;

import com.node5.catalogservice.inventory.application.dto.StockCommitCommand;

import jakarta.validation.constraints.NotNull;

public record StockCommitRequest(
	@NotNull
	UUID orderId,

	@NotNull
	UUID productId
) {
	public StockCommitCommand toCommand() {
		return new StockCommitCommand(orderId, productId);
	}
}
