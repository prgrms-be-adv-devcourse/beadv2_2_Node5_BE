package com.node5.catalogservice.inventory.application.dto;

import java.util.UUID;

public record StockReleaseCommand(
	UUID orderId,
	UUID productId
) {
}
