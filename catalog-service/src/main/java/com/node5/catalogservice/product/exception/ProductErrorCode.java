package com.node5.catalogservice.product.exception;

import com.node5.common.exception.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements BaseErrorCode {

	PRODUCT_NOT_FOUND(404, "PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."),
	ON_SALE_PRODUCT_NOT_FOUND(404, "ON_SALE_PRODUCT_NOT_FOUND", "판매 중인 상품이 아니거나 존재하지 않습니다."),
	SHOP_NOT_OWNED(403, "SHOP_NOT_OWNED", "상점이 존재하지 않거나, 내 상점이 아닙니다.");

	private final int status;
	private final String code;
	private final String message;
}
