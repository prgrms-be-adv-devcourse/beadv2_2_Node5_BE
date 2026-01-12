package com.node5.catalogservice.cart.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.node5.catalogservice.cart.domain.CartItem;
import com.node5.catalogservice.product.domain.Product;

public record CartItemInfo(
	UUID id,
	UUID productId,
	String name,
	Long price,
	String thumbnailUrl,
	int quantity,
	LocalDateTime createdAt,
	LocalDateTime modifiedAt
) {

	public static CartItemInfo from(CartItem item, Product product) {
		return new CartItemInfo(
			item.getId(),
			item.getProductId(),
			product.getName(),
			product.getPrice().longValue(),
			product.getThumbnailKey(),
			item.getQuantity(),
			item.getCreatedAt(),
			item.getModifiedAt()
		);
	}
}
