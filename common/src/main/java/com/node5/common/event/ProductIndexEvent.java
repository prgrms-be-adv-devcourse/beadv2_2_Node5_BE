package com.node5.common.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductIndexEvent(
	UUID productId,
	UUID shopId,
	String name,
	String nameAutocomplete,
	String category,
	String thumbnailKey,
	long price,
	String status,
	LocalDateTime createdAt,
	ProductIndexEventType type
) {
}
