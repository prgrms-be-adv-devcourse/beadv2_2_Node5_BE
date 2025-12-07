package com.node5.catalogservice.cart.presentation.dto;

import com.node5.catalogservice.cart.application.dto.CartItemUpdateCommand;

public record CartItemUpdateRequest(Integer quantity) {

	public CartItemUpdateCommand toCommand() {
		if (quantity == null) {
			throw new IllegalArgumentException("quantity is required");
		}
		return new CartItemUpdateCommand(quantity);
	}
}
