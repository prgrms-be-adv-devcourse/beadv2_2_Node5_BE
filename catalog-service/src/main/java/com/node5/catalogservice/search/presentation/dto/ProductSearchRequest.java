package com.node5.catalogservice.search.presentation.dto;

import com.node5.catalogservice.search.domain.ProductSearchSort;

public record ProductSearchRequest(
	String keyword,
	String category,
	String shopId,
	Integer minPrice,
	Integer maxPrice,
	ProductSearchSort sort
) {
}
