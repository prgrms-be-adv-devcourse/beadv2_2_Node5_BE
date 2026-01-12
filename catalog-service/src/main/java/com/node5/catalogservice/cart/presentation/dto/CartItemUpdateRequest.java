package com.node5.catalogservice.cart.presentation.dto;

import com.node5.catalogservice.cart.application.dto.CartItemUpdateCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemUpdateRequest(
	@Schema(description = "변경할 수량", example = "3")
	@NotNull(message = "quantity는 필수입니다.")
	@Positive(message = "quantity는 1 이상이어야 합니다.")
	Integer quantity
) {

	public CartItemUpdateCommand toCommand() {
		return new CartItemUpdateCommand(quantity);
	}
}
