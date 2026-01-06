package com.node5.catalogservice.product.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductCategory;
import com.node5.catalogservice.product.domain.ProductStatus;

public record ProductInfo(
	UUID id,
	UUID shopId,
	String name,
	String description,
	BigDecimal price,
	ProductStatus status,
	ProductCategory category,
	String thumbnailUrl,
	LocalDateTime createdAt,
	LocalDateTime modifiedAt
) {

	public static ProductInfo from(Product product) {
		return new ProductInfo(
			product.getId(),
			product.getShopId(),
			product.getName(),
			product.getDescription(),
			product.getPrice(),
			product.getStatus(),
			product.getCategory(),
			product.getThumbnailUrl(),
			product.getCreatedAt(),
			product.getModifiedAt()
		);
	}
}
