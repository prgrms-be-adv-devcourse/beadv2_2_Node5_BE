package com.node5.catalogservice.inventory.presentation.dto;

import java.util.UUID;

import com.node5.catalogservice.inventory.application.dto.StockReleaseBatchCommand;

import jakarta.validation.constraints.NotNull;

public record StockReleaseItemRequest(
	@NotNull UUID productId
) {
	public StockReleaseBatchCommand.Item toCommand() {
		return new StockReleaseBatchCommand.Item(productId);
	}
}
