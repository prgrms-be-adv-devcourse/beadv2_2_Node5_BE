package com.node5.catalogservice.product.presentation.dto;

import com.node5.catalogservice.product.domain.ProductStatus;

public record StatusRequest(ProductStatus status) {
}
