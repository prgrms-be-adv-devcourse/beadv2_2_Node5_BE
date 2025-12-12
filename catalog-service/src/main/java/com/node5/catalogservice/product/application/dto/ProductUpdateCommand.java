package com.node5.catalogservice.product.application.dto;

import java.math.BigDecimal;

import com.node5.catalogservice.product.domain.ProductCategory;

public record ProductUpdateCommand(
	String name,
	String description,
	BigDecimal price,
	Integer stock,
	ProductCategory category,
	String thumbnailUrl
) {
}
