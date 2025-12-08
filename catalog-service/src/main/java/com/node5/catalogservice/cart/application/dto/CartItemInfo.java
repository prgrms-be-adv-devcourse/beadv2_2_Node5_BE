package com.node5.catalogservice.cart.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.node5.catalogservice.cart.domain.CartItem;

public record CartItemInfo(
	UUID id,
	UUID memberId,
	UUID productId,
	int quantity,
	LocalDateTime createdAt,
	LocalDateTime modifiedAt
) {

	public static CartItemInfo from(CartItem item) {
		return new CartItemInfo(
			item.getId(),
			item.getMemberId(),
			item.getProductId(),
			item.getQuantity(),
			item.getCreatedAt(),
			item.getModifiedAt()
		);
	}
}
