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
	LocalDateTime createdAt
) {

	public static ProductIndexEvent from(Product product) {
		return new ProductIndexEvent(
			product.getId(),
			product.getName(),
			product.getCategory(),
			product.getPrice().longValue(),
			product.getStatus().name(),
			product.getCreatedAt()
		);
	}
}
