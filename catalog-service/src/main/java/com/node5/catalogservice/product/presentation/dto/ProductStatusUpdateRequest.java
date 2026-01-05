package com.node5.catalogservice.product.presentation.dto;

import com.node5.catalogservice.product.domain.ProductStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "상품 상태 변경 요청")
public record ProductStatusUpdateRequest(
	@NotNull
	@Schema(description = "변경할 상품 상태", example = "HIDDEN")
	ProductStatus status
) {
}
