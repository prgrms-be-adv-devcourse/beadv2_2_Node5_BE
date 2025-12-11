package com.node5.catalogservice.search.application.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 검색 응답")
public record ProductSearchResponse(
	@Schema(description = "상품 ID", example = "8b3f6c4d-1a2b-4c5d-9e8f-0a1b2c3d4e5f")
	String productId,
	@Schema(description = "상품명", example = "린넨 셔츠")
	String name,
	@Schema(description = "카테고리 코드", example = "TOP")
	String category,
	@Schema(description = "썸네일 경로 (S3 Key)", example = "product/72cb3062-c897-45bd-9670-bdeac58e6a11-thumbnail.png")
	String thumbnailUrl,
	@Schema(description = "가격", example = "19900")
	Long price,
	@Schema(description = "상품 상태", example = "ON_SALE")
	String status,
	@Schema(description = "상품 등록일시", example = "2024-07-01T12:34:56")
	LocalDateTime createdAt
) {
}
