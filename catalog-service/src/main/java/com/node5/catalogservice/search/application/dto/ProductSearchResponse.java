package com.node5.catalogservice.search.application.dto;

public record ProductSearchResponse(
	String productId,
	String name,
	String category,
	Long price,
	String status
) {
}
