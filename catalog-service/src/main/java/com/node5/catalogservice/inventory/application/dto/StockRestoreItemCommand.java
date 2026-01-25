package com.node5.catalogservice.inventory.application.dto;

import java.util.UUID;

public record StockRestoreItemCommand(
	UUID productId,
	int quantity
) {
}
