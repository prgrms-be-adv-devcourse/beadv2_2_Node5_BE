package com.node5.supportservice.search.infrastructure.client.dto;

import java.util.List;

public record ProductIndexSummaryListResponse(
	List<ProductIndexSummaryResponse> products
) {
}
