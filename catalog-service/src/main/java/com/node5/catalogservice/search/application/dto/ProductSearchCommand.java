package com.node5.catalogservice.search.application.dto;

import java.util.UUID;

import com.node5.catalogservice.product.domain.ProductCategory;
import com.node5.catalogservice.search.domain.ProductSearchSort;

public record ProductSearchCommand(
	String keyword,
	UUID shopId,
	ProductCategory category,
	Integer minPrice,
	Integer maxPrice,
	ProductSearchSort sort
) {
}
