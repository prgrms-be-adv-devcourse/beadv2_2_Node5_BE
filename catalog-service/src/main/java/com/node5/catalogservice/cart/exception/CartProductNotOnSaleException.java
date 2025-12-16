package com.node5.catalogservice.cart.exception;

import com.node5.common.exception.BaseException;

public class CartProductNotOnSaleException extends BaseException {
	public CartProductNotOnSaleException() {
		super(CartErrorCode.CART_PRODUCT_NOT_ON_SALE);
	}
}
