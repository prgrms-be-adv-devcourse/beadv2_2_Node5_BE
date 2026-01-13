package com.node5.common.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductDiscontinuedEvent(
	UUID eventId,
	UUID productId,
	LocalDateTime modifiedAt,
	LocalDateTime occurredAt
) {
}
