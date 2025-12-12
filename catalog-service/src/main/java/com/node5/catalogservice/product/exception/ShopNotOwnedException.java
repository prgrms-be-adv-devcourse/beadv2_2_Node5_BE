package com.node5.catalogservice.product.exception;

import com.node5.common.exception.BaseException;

public class ShopNotOwnedException extends BaseException {
	public ShopNotOwnedException() {
		super(ProductErrorCode.SHOP_NOT_OWNED);
	}
}
