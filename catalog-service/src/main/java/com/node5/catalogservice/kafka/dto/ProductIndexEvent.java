package com.node5.catalogservice.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.node5.catalogservice.product.domain.Product;

/**
 * Kafka 전송용 DTO
 */

public record ProductIndexEvent(
	UUID productId,
	String name,
	String category,
	long price,
	String status,
	LocalDateTime createdAt,
	ProductIndexEventType type
) {

	public static ProductIndexEvent create(Product product) {
		return new ProductIndexEvent(
			product.getId(),
			product.getName(),
			product.getCategory(),
			product.getPrice().longValue(),
			product.getStatus().name(),
			product.getCreatedAt(),
			ProductIndexEventType.CREATE
		);
	}

	public static ProductIndexEvent update(Product product) {
		return new ProductIndexEvent(
			product.getId(),
			product.getName(),
			product.getCategory(),
			product.getPrice().longValue(),
			product.getStatus().name(),
			product.getCreatedAt(),
			ProductIndexEventType.UPDATE
		);
	}
}
