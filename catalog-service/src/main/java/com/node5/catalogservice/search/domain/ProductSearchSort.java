package com.node5.catalogservice.search.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 검색 정렬 기준")
public enum ProductSearchSort {
	@Schema(description = "최신 등록순")
	LATEST,
	@Schema(description = "낮은 가격순")
	LOW_PRICE,
	@Schema(description = "높은 가격순")
	HIGH_PRICE;
}
