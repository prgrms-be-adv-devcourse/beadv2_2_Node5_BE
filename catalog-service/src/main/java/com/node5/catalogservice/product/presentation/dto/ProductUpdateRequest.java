package com.node5.catalogservice.product.presentation.dto;

import java.math.BigDecimal;

import com.node5.catalogservice.product.application.dto.ProductUpdateCommand;
import com.node5.catalogservice.product.domain.ProductCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "상품 수정 요청")
public record ProductUpdateRequest(

	@Schema(description = "상품명", example = "노이즈 캔슬링 헤드폰")
	@NotBlank(message = "상품명은 필수입니다.")
	String name,

	@Schema(description = "상품 설명", example = "프리미엄 사운드와 액티브 노이즈 캔슬링 기능 제공")
	String description,

	@Schema(description = "가격", example = "199000")
	@NotNull(message = "가격은 필수입니다.")
	@Positive(message = "가격은 0보다 커야 합니다.")
	BigDecimal price,

	@Schema(description = "카테고리", example = "ELECTRONICS_DIGITAL")
	@NotNull(message = "카테고리는 필수입니다.")
	ProductCategory category,

	@Schema(description = "대표 이미지 객체 키", example = "product/550e8400-e29b-41d4-a716-446655440000")
	String thumbnailKey
) {

	public ProductUpdateCommand toCommand() {
		return new ProductUpdateCommand(
			name,
			description,
			price,
			category,
			thumbnailKey
		);
	}
}
