package com.node5.catalogservice.inventory.application.dto;

import java.util.List;
import java.util.UUID;

public record StockCommitBatchResult(
	UUID orderId,
	List<UUID> committedProductIds
) {
	public static StockCommitBatchResult of(UUID orderId, List<UUID> committedProductIds) {
		return new StockCommitBatchResult(orderId, committedProductIds);
	}
}
