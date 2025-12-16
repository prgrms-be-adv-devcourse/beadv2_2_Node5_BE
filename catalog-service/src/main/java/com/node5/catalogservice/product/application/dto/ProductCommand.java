package com.node5.catalogservice.product.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.node5.catalogservice.product.domain.ProductCategory;
import com.node5.catalogservice.product.domain.ProductStatus;

public record ProductCommand(
	UUID shopId,
	String name,
	String description,
	BigDecimal price,
	Integer stock,
	ProductStatus status,
	ProductCategory category,
	String thumbnailUrl
) {
}
