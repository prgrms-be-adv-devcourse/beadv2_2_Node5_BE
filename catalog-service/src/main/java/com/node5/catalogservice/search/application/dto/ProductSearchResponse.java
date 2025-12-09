package com.node5.catalogservice.search.application.dto;

import java.time.LocalDateTime;

public record ProductSearchResponse(
	String productId,
	String name,
	String category,
	Long price,
	String status,
	LocalDateTime createdAt
) {
}
