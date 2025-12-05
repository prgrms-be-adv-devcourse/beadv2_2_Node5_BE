package com.node5.catalogservice.product.presentation.dto;

import com.node5.catalogservice.product.domain.ProductStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 상태 변경 요청")
public record StatusRequest(ProductStatus status) {
}
