package com.node5.catalogservice.product.presentation.dto;

import java.math.BigDecimal;

import com.node5.catalogservice.product.application.dto.ProductUpdateCommand;

public record ProductUpdateRequest(
	String name,
	String description,
	BigDecimal price,
	Integer stock,
	String category,
	String thumbnailUrl
) {

	public ProductUpdateCommand toCommand() {
		return new ProductUpdateCommand(
			name,
			description,
			price,
			stock,
			category,
			thumbnailUrl
		);
	}
}
