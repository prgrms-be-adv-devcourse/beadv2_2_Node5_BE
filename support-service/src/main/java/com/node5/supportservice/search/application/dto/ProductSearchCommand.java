package com.node5.supportservice.search.application.dto;

import java.util.UUID;

import com.node5.supportservice.search.domain.ProductSearchSort;

public record ProductSearchCommand(
	String keyword,
	UUID shopId,
	ProductCategoryCode category,
	Integer minPrice,
	Integer maxPrice,
	ProductSearchSort sort
) {
}
