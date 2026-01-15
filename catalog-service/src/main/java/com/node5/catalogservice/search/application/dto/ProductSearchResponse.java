package com.node5.catalogservice.search.application.dto;

import java.time.LocalDateTime;

import com.node5.catalogservice.search.domain.ProductDocument;

public record ProductSearchResponse(
	String productId,
	String shopId,
	String name,
	String category,
	String thumbnailKey,
	Long price,
	String status,
	LocalDateTime createdAt
) {

	public static ProductSearchResponse from(ProductDocument doc) {
		return new ProductSearchResponse(
			doc.getProductId(),
			doc.getShopId(),
			doc.getName(),
			doc.getCategory(),
			doc.getThumbnailKey(),
			doc.getPrice(),
			doc.getStatus(),
			doc.getCreatedAt()
		);
	}
}
