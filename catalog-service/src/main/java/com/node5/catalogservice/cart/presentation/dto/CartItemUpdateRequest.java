package com.node5.catalogservice.cart.presentation.dto;

import com.node5.catalogservice.cart.application.dto.CartItemUpdateCommand;

import io.swagger.v3.oas.annotations.media.Schema;

public record CartItemUpdateRequest(
	@Schema(description = "변경할 수량", example = "3")
	Integer quantity
) {

	public CartItemUpdateCommand toCommand() {
		if (quantity == null) {
			throw new IllegalArgumentException("수량은 필수 값입니다.");
		}
		return new CartItemUpdateCommand(quantity);
	}
}
