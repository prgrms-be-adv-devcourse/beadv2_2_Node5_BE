package com.node5.catalogservice.cart.exception;

import com.node5.common.exception.BaseException;

public class CartProductNotFoundException extends BaseException {
	public CartProductNotFoundException() {
		super(CartErrorCode.CART_PRODUCT_NOT_FOUND);
	}
}
