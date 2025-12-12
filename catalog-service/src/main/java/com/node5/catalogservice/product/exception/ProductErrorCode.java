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

	PRODUCT_STATUS_CHANGE_NOT_ALLOWED(409, "PRODUCT_STATUS_CHANGE_NOT_ALLOWED", "중단된 상품은 상태를 변경할 수 없습니다."),
	PRODUCT_INVALID_STOCK(400, "PRODUCT_INVALID_STOCK", "재고 수량은 0 이상이어야 합니다.");

	private final int status;
	private final String code;
	private final String message;
}
