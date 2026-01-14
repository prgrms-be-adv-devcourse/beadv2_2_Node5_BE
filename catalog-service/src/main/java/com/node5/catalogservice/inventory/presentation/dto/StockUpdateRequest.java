package com.node5.catalogservice.inventory.presentation.dto;

import jakarta.validation.constraints.Min;

public record StockUpdateRequest(
	@Min(0)
	int quantity
) {
}
