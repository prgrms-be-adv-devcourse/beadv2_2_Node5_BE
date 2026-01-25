package com.node5.catalogservice.inventory.application.dto;

import java.util.List;
import java.util.UUID;

public record StockRestoreBatchCommand(
	UUID orderId,
	UUID cancelEventId,
	List<StockRestoreItemCommand> items
) {
}
