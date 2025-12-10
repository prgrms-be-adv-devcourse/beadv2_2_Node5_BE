package com.node5.catalogservice.search.presentation.dto;

import com.node5.catalogservice.search.domain.ProductSearchSort;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 검색 요청")
public record ProductSearchRequest(
	@Schema(description = "상품명 검색 키워드", example = "셔츠", required = false)
	String keyword,
	@Schema(description = "카테고리", example = "TOP", required = false)
	String category,
	@Schema(description = "최소 가격", example = "10000", required = false)
	Integer minPrice,
	@Schema(description = "최대 가격", example = "30000", required = false)
	Integer maxPrice,
	@Schema(description = "정렬 기준", implementation = ProductSearchSort.class, required = false)
	ProductSearchSort sort
) {
}
