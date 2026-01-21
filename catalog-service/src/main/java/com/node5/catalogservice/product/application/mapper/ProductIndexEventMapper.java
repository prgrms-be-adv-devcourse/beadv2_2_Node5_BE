package com.node5.catalogservice.product.application.mapper;

import com.node5.catalogservice.product.domain.Product;
import com.node5.common.event.ProductIndexEvent;
import com.node5.common.event.ProductIndexEventType;

public final class ProductIndexEventMapper {

	private ProductIndexEventMapper() {
	}

	public static ProductIndexEvent forCreate(Product product) {
		return map(product, ProductIndexEventType.CREATE);
	}

	public static ProductIndexEvent forUpdate(Product product) {
		return map(product, ProductIndexEventType.UPDATE);
	}

	private static ProductIndexEvent map(Product product, ProductIndexEventType type) {
		return new ProductIndexEvent(
			product.getId(),
			product.getShopId(),
			product.getName(),
			product.getName(),
			product.getCategory().name(),
			product.getThumbnailKey(),
			product.getPrice().longValue(),
			product.getStatus().name(),
			product.getCreatedAt(),
			product.getModifiedAt(),
			type
		);
	}
}
