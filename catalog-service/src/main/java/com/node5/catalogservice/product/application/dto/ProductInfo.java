package com.node5.catalogservice.product.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductStatus;

public record ProductInfo(
	UUID id,
	UUID sellerId,
	String name,
	String description,
	BigDecimal price,
	Integer stock,
	ProductStatus status,
	String category,
	String thumbnailUrl,
	LocalDateTime createdAt,
	LocalDateTime modifiedAt
) {

	public static ProductInfo from(Product product) {
		return new ProductInfo(
			product.getId(),
			product.getSellerId(),
			product.getName(),
			product.getDescription(),
			product.getPrice(),
			product.getStock(),
			product.getStatus(),
			product.getCategory(),
			product.getThumbnailUrl(),
			product.getCreatedAt(),
			product.getModifiedAt()
		);
	}
}
