package com.node5.catalogservice.inventory.presentation.dto;

import java.util.UUID;

import com.node5.catalogservice.inventory.application.dto.StockCommitBatchCommand;

import jakarta.validation.constraints.NotNull;

public record StockCommitItemRequest(
	@NotNull
	UUID productId
) {
	public StockCommitBatchCommand.Item toCommand() {
		return new StockCommitBatchCommand.Item(productId);
	}
}
