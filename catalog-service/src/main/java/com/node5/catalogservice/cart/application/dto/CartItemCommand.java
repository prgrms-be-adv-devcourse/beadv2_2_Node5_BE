package com.node5.catalogservice.cart.application.dto;

import java.util.UUID;

public record CartItemCommand(
	UUID memberId,
	UUID productId,
	int quantity
) {
}
