package com.node5.catalogservice.product.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 카테고리")
public enum ProductCategory {
	@Schema(description = "식품/음료")
	FOOD_BEVERAGE,

	@Schema(description = "패션/뷰티")
	FASHION_BEAUTY,

	@Schema(description = "생활/가전")
	HOME_APPLIANCES,

	@Schema(description = "전자/디지털")
	ELECTRONICS_DIGITAL,

	@Schema(description = "취미/레저")
	HOBBY_LEISURE,

	@Schema(description = "헬스/피트니스")
	HEALTH_FITNESS,

	@Schema(description = "서비스/구독")
	SERVICE_SUBSCRIPTION
}
