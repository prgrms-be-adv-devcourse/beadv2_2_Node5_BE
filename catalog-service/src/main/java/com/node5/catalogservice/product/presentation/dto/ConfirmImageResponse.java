package com.node5.catalogservice.product.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 이미지 업로드 확정 응답")
public record ConfirmImageResponse(
	@Schema(description = "확정된 상품 이미지 객체 키", example = "product/550e8400-e29b-41d4-a716-446655440000")
	String productKey
) {
}
