package com.node5.catalogservice.product.application.dto;

import java.math.BigDecimal;

import com.node5.catalogservice.product.domain.ProductCategory;
import com.node5.catalogservice.product.domain.ProductStatus;

public record ProductCommand(
	String name,
	String description,
	BigDecimal price,
	ProductStatus status,
	ProductCategory category,
	String thumbnailUrl
) {
}
