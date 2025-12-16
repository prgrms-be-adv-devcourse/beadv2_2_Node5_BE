package com.node5.catalogservice.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.node5.catalogservice.product.domain.Product;

/**
 * 상품 변경 사항을 Elasticsearch 색인에 반영하기 위한 이벤트 DTO입니다.
 * <p>
 * 상품 생성/수정 시 Kafka를 통해 발행되며, 이벤트 타입에 따라 색인 작업이 구분됩니다.
 */
public record ProductIndexEvent(
	UUID productId,
	UUID shopId,
	String name,
	String category,
	String thumbnailUrl,
	long price,
	String status,
	LocalDateTime createdAt,
	ProductIndexEventType type
) {

	public static ProductIndexEvent create(Product product) {
		return new ProductIndexEvent(
			product.getId(),
			product.getShopId(),
			product.getName(),
			product.getCategory().name(),
			product.getThumbnailUrl(),
			product.getPrice().longValue(),
			product.getStatus().name(),
			product.getCreatedAt(),
			ProductIndexEventType.CREATE
		);
	}

	public static ProductIndexEvent update(Product product) {
		return new ProductIndexEvent(
			product.getId(),
			product.getShopId(),
			product.getName(),
			product.getCategory().name(),
			product.getThumbnailUrl(),
			product.getPrice().longValue(),
			product.getStatus().name(),
			product.getCreatedAt(),
			ProductIndexEventType.UPDATE
		);
	}
}
