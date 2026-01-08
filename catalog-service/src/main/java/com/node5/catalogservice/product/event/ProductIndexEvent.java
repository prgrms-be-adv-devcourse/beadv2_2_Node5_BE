package com.node5.catalogservice.product.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.node5.catalogservice.product.domain.Product;

public record ProductIndexEvent(
	UUID productId,
	UUID shopId,
	String name,
	String nameAutocomplete,
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
