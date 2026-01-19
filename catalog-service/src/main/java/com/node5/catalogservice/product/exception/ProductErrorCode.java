package com.node5.catalogservice.product.exception;

import com.node5.common.exception.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements BaseErrorCode {

	PRODUCT_NOT_FOUND(404, "PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."),
	ON_SALE_PRODUCT_NOT_FOUND(404, "ON_SALE_PRODUCT_NOT_FOUND", "판매 중인 상품이 아니거나 존재하지 않습니다."),

	SHOP_NOT_FOUND(404, "SHOP_NOT_FOUND", "상점을 찾을 수 없습니다."),
	SHOP_FORBIDDEN(403, "SHOP_FORBIDDEN", "해당 상점에 대한 권한이 없습니다."),

	PRODUCT_STATUS_CHANGE_NOT_ALLOWED(409, "PRODUCT_STATUS_CHANGE_NOT_ALLOWED", "판매 중단된 상품은 수정/상태 변경이 불가합니다."),

	SHOP_SERVICE_UNAVAILABLE(503, "SHOP_SERVICE_UNAVAILABLE", "상점 정보를 조회할 수 없습니다. 잠시 후 다시 시도해주세요.");

	private final int status;
	private final String code;
	private final String message;
}
