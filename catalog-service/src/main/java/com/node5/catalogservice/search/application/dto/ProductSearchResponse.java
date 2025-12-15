package com.node5.catalogservice.search.application.dto;

import java.time.LocalDateTime;

import com.node5.catalogservice.search.domain.ProductDocument;

/**
 * 상품 검색 유스케이스의 조회 결과를 표현하는 DTO입니다.
 * <p>
 * Elasticsearch 문서(ProductDocument)를 기반으로 생성됩니다.
 */
public record ProductSearchResponse(
	String productId,
	String shopId,
	String name,
	String category,
	String thumbnailUrl,
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
			doc.getThumbnailUrl(),
			doc.getPrice(),
			doc.getStatus(),
			doc.getCreatedAt()
		);
	}
}
