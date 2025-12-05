package com.node5.catalogservice.product.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.node5.catalogservice.product.application.dto.ProductCommand;
import com.node5.catalogservice.product.application.dto.ProductUpdateCommand;
import com.node5.catalogservice.product.domain.ProductStatus;

public record ProductRequest(
	String sellerId,
	String name,
	String description,
	BigDecimal price,
	Integer stock,
	ProductStatus status,
	String category,
	String thumbnailUrl
) {

	public ProductCommand toCreateCommand() {

		if (sellerId == null || sellerId.isBlank()) {
			throw new IllegalArgumentException("Seller id cannot be null or blank");
		}

		UUID seller;
		try {
			seller = UUID.fromString(sellerId);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Seller id is not a valid UUID");
		}

		return new ProductCommand(
			seller,
			name,
			description,
			price,
			stock,
			status,
			category,
			thumbnailUrl
		);
	}

	public ProductUpdateCommand toUpdateCommand() {
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
