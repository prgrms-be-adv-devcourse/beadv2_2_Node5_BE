package com.node5.catalogservice.inventory.application.dto;

import java.util.UUID;

public record StockRegisterCommand(
	UUID productId,
	int quantity
) {
}
