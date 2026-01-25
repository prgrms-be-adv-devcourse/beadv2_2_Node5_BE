package com.node5.supportservice.search.infrastructure.client.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductIndexSummaryResponse(
	UUID productId,
	UUID shopId,
	String name,
	String nameAutocomplete,
	String category,
	String thumbnailKey,
	long price,
	String status,
	LocalDateTime createdAt,
	LocalDateTime modifiedAt
) {
}

