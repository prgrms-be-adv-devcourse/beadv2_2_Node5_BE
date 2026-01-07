package com.node5.catalogservice.search.application.dto;

import com.node5.catalogservice.product.domain.ProductCategory;

public record ProductAutocompleteCommand(
	String keyword,
	ProductCategory category,
	Integer size
) {
}
