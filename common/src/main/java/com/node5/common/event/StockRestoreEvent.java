package com.node5.common.event;

import java.util.List;
import java.util.UUID;

public record StockRestoreEvent(
	UUID cancelEventId,
	UUID orderId,
	List<Item> items
) {
	public record Item(UUID productId, int quantity) {
	}
}
