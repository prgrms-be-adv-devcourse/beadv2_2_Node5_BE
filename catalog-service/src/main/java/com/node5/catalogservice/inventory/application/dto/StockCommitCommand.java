package com.node5.catalogservice.inventory.application.dto;

import java.util.UUID;

public record StockCommitCommand(
	UUID orderId,
	UUID productId
) {
}
