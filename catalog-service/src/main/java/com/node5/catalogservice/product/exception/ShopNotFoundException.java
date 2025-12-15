package com.node5.catalogservice.product.exception;

import com.node5.common.exception.BaseException;

public class ShopNotFoundException extends BaseException {
	public ShopNotFoundException() {
		super(ProductErrorCode.SHOP_NOT_FOUND);
	}
}
