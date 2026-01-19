package com.node5.catalogservice.inventory.application.dto;

import java.util.List;
import java.util.UUID;

public record StockCommitBatchCommand(
	UUID orderId,
	List<Item> items
) {
	public record Item(UUID productId) {
	}
}
