package com.node5.catalogservice.cart.exception;

import com.node5.common.exception.BaseException;

public class CartItemForbiddenException extends BaseException {
	public CartItemForbiddenException() {
		super(CartErrorCode.CART_ITEM_FORBIDDEN);
	}
}
