package com.node5.catalogservice.inventory.presentation.dto;

import java.util.List;
import java.util.UUID;

import com.node5.catalogservice.inventory.application.dto.StockHoldBatchCommand;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StockHoldBatchRequest(
	@NotNull
	UUID orderId,

	@Valid
	@NotNull
	@Size(min = 1)
	List<StockHoldItemRequest> items
) {
	public StockHoldBatchCommand toCommand() {
		return new StockHoldBatchCommand(
			orderId,
			items.stream().map(StockHoldItemRequest::toCommand).toList()
		);
	}
}
