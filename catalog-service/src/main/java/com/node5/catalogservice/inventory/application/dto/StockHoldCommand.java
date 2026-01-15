package com.node5.catalogservice.inventory.application.dto;

import java.util.UUID;

public record StockHoldCommand(
	UUID orderId,
	UUID productId,
	int quantity
) {
}
