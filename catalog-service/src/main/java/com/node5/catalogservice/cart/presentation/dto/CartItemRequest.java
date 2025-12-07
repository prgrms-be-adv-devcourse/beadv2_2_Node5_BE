package com.node5.catalogservice.cart.presentation.dto;

import java.util.UUID;

import com.node5.catalogservice.cart.application.dto.CartItemCommand;

public record CartItemRequest(
	String memberId,
	String productId,
	Integer quantity
) {

	public CartItemCommand toCommand() {
		if (memberId == null || memberId.isBlank()) {
			throw new IllegalArgumentException("memberId is required");
		}
		if (productId == null || productId.isBlank()) {
			throw new IllegalArgumentException("productId is required");
		}
		if (quantity == null) {
			throw new IllegalArgumentException("quantity is required");
		}

		UUID member = UUID.fromString(memberId);
		UUID product = UUID.fromString(productId);

		return new CartItemCommand(member, product, quantity);
	}
}
