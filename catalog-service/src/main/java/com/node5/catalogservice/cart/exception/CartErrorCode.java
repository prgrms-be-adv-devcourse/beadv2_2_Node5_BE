package com.node5.catalogservice.cart.exception;

import com.node5.common.exception.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements BaseErrorCode {

	CART_PRODUCT_NOT_FOUND(404, "CART_PRODUCT_NOT_FOUND", "장바구니에 담으려는 상품이 존재하지 않습니다."),
	CART_PRODUCT_NOT_ON_SALE(400, "CART_PRODUCT_NOT_ON_SALE", "해당 상품은 장바구니에 담을 수 없는 상태입니다."),
	CART_ITEM_NOT_FOUND(404, "CART_ITEM_NOT_FOUND", "장바구니 항목이 존재하지 않습니다."),
	CART_QUANTITY_INVALID(400, "CART_QUANTITY_INVALID", "수량은 1 이상이어야 합니다.");

	private final int status;
	private final String code;
	private final String message;
}
