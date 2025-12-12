package com.node5.catalogservice.cart.exception;

import com.node5.common.exception.BaseException;

public class CartItemNotFoundException extends BaseException {
	public CartItemNotFoundException() {
		super(CartErrorCode.CART_ITEM_NOT_FOUND);
	}
}
