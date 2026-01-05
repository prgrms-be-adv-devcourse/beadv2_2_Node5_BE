package com.node5.catalogservice.product.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductStockUpdateRequest(
	@NotNull
	@PositiveOrZero
	Integer stock
) {
}
