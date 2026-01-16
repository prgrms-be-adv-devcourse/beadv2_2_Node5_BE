package com.node5.catalogservice.inventory.presentation.dto;

import java.util.List;
import java.util.UUID;

import com.node5.catalogservice.inventory.application.dto.StockCommitBatchCommand;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StockCommitBatchRequest(
	@NotNull
	UUID orderId,

	@Valid
	@NotNull
	@Size(min = 1)
	List<StockCommitItemRequest> items
) {
	public StockCommitBatchCommand toCommand() {
		return new StockCommitBatchCommand(
			orderId,
			items.stream().map(StockCommitItemRequest::toCommand).toList()
		);
	}
}
