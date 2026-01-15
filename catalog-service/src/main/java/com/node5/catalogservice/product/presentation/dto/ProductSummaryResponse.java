package com.node5.catalogservice.product.presentation.dto;

import java.util.UUID;

public record ProductSummaryResponse(
	UUID productId,
	String name,
	String category,
	String description
) {
}
