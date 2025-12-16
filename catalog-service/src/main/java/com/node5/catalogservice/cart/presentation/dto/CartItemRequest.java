package com.node5.catalogservice.cart.presentation.dto;

import java.util.UUID;

import com.node5.catalogservice.cart.application.dto.CartItemCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequest(
	@Schema(description = "상품 ID", example = "c2f0d8b7-5678-4e12-9abc-0def12345678")
	@NotNull(message = "productId는 필수입니다.")
	UUID productId,

	@Schema(description = "담을 상품 수량", example = "2")
	@NotNull(message = "quantity는 필수입니다.")
	@Positive(message = "quantity는 1 이상이어야 합니다.")
	Integer quantity
) {

	public CartItemCommand toCommand(UUID memberId) {
		return new CartItemCommand(memberId, productId, quantity);
	}
}
