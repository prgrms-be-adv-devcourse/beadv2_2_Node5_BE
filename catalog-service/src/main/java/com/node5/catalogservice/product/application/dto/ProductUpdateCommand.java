package com.node5.catalogservice.product.application.dto;

import java.math.BigDecimal;

public record ProductUpdateCommand(
	String name,
	String description,
	BigDecimal price,
	Integer stock,
	String category,
	String thumbnailUrl
) {
}
