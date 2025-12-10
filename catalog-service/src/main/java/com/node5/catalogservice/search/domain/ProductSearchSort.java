package com.node5.catalogservice.search.domain;

import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 검색 정렬 기준")
public enum ProductSearchSort {
	@Schema(description = "최신 등록순")
	LATEST,
	@Schema(description = "낮은 가격순")
	LOW_PRICE,
	@Schema(description = "높은 가격순")
	HIGH_PRICE;

	public Sort toSort() {
		return switch (this) {
			case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
			case LOW_PRICE -> Sort.by(Sort.Direction.ASC, "price");
			case HIGH_PRICE -> Sort.by(Sort.Direction.DESC, "price");
		};
	}
}
