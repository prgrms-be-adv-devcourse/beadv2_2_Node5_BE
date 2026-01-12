package com.node5.catalogservice.cart.application.dto;

import java.util.UUID;

public record CartItemCommand(
	UUID productId,
	Integer quantity
) {
}
