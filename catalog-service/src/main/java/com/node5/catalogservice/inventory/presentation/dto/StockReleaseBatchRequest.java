package com.node5.catalogservice.inventory.presentation.dto;

import java.util.List;
import java.util.UUID;

import com.node5.catalogservice.inventory.application.dto.StockReleaseBatchCommand;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StockReleaseBatchRequest(
	@NotNull UUID orderId,

	@Valid
	@NotNull
	@Size(min = 1)
	List<StockReleaseItemRequest> items
) {
	public StockReleaseBatchCommand toCommand() {
		return new StockReleaseBatchCommand(
			orderId,
			items.stream().map(StockReleaseItemRequest::toCommand).toList()
		);
	}
}
