package com.node5.common.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductEmbeddingEvent(
	UUID eventId,
	UUID productId,
	String name,
	String description,
	String category,
	String status,
	LocalDateTime modifiedAt,
	LocalDateTime occurredAt
) {
}
