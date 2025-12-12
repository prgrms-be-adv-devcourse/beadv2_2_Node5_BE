package com.node5.catalogservice.product.presentation.dto;

import java.math.BigDecimal;

import com.node5.catalogservice.product.application.dto.ProductUpdateCommand;
import com.node5.catalogservice.product.domain.ProductCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "상품 수정 요청")
public record ProductUpdateRequest(
	String name,
	String description,

	@Positive(message = "가격은 0보다 커야 합니다.")
	BigDecimal price,

	@PositiveOrZero(message = "재고 수량은 0 이상이어야 합니다.")
	Integer stock,

	ProductCategory category,
	String thumbnailUrl
) {

	public ProductUpdateCommand toCommand() {
		return new ProductUpdateCommand(
			name,
			description,
			price,
			stock,
			category,
			thumbnailUrl
		);
	}
}
