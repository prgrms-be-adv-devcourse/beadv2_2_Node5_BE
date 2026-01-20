package com.node5.supportservice.search.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 카테고리 코드")
public enum ProductCategoryCode {
	FOOD_BEVERAGE,
	FASHION_BEAUTY,
	HOME_APPLIANCES,
	ELECTRONICS_DIGITAL,
	HOBBY_LEISURE,
	HEALTH_FITNESS
}
