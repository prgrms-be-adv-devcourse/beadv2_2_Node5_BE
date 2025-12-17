package com.node5.catalogservice.product.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 판매 상태")
public enum ProductStatus {
	@Schema(description = "판매 중")
	ON_SALE,

	@Schema(description = "숨김(비노출)")
	HIDDEN,

	@Schema(description = "판매 중단")
	DISCONTINUED
}
