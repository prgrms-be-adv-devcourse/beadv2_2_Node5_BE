package com.node5.catalogservice.product.application.mapper;

import java.time.LocalDateTime;
import java.util.UUID;

import com.node5.catalogservice.product.domain.Product;
import com.node5.common.event.ProductEmbeddingEvent;

public final class ProductEmbeddingEventMapper {

	private ProductEmbeddingEventMapper() {
	}

	public static ProductEmbeddingEvent from(Product product) {
		return new ProductEmbeddingEvent(
			UUID.randomUUID(),
			product.getId(),
			product.getName(),
			product.getDescription(),
			product.getCategory().name(),
			product.getStatus().name(),
			product.getModifiedAt(),
			LocalDateTime.now()
		);
	}
}
