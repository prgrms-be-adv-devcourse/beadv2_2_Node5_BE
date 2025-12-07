package com.node5.catalogservice.cart.presentation.dto;

import java.util.UUID;

import com.node5.catalogservice.cart.application.dto.CartItemCommand;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartItemRequest(
	@Schema(description = "회원 ID (UUID 문자열)", example = "b1a0e5c4-1234-4c56-9abc-0def12345678")
	String memberId,
	@Schema(description = "상품 ID (UUID 문자열)", example = "c2f0d8b7-5678-4e12-9abc-0def12345678")
	String productId,
	@Schema(description = "담을 상품 수량", example = "2")
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
