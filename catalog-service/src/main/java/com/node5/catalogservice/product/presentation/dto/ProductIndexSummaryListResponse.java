package com.node5.catalogservice.product.presentation.dto;

import java.util.List;

public record ProductIndexSummaryListResponse(
	List<ProductIndexSummaryResponse> products
) {
}
