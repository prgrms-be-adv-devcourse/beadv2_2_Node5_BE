package com.node5.catalogservice.product.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "상품 이미지 업로드 확정 요청")
public record ConfirmImageRequest(
	@NotBlank
	@Schema(description = "업로드가 완료된 임시 이미지 객체 키", example = "temp/product/550e8400-e29b-41d4-a716-446655440000")
	String tempKey
) {
}
