package com.node5.catalogservice.product.presentation.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.node5.catalogservice.product.application.dto.ProductCommand;
import com.node5.catalogservice.product.domain.ProductCategory;
import com.node5.catalogservice.product.domain.ProductStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 등록/수정 요청")
public record ProductRequest(
	@Schema(description = "상점 ID (UUID 문자열)", example = "b1a0e5c4-1234-4c56-9abc-0def12345678")
	String shopId,
	@Schema(description = "상품명", example = "노이즈 캔슬링 헤드폰")
	String name,
	@Schema(description = "상품 설명", example = "프리미엄 사운드와 액티브 노이즈 캔슬링 기능 제공")
	String description,
	@Schema(description = "가격", example = "199000")
	BigDecimal price,
	@Schema(description = "재고 수량", example = "50")
	Integer stock,
	@Schema(description = "상품 상태", example = "ON_SALE")
	ProductStatus status,
	@Schema(description = "카테고리", example = "전자기기")
	ProductCategory category,
	@Schema(description = "대표 이미지 URL", example = "https://cdn.example.com/product/thumbnail.jpg")
	String thumbnailUrl
) {

	public ProductCommand toCommand() {

		if (shopId == null || shopId.isBlank()) {
			throw new IllegalArgumentException("Shop id cannot be null or blank");
		}

		UUID shop;
		try {
			shop = UUID.fromString(shopId);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Shop id is not a valid UUID");
		}

		return new ProductCommand(
			shop,
			name,
			description,
			price,
			stock,
			status,
			category,
			thumbnailUrl
		);
	}
}
