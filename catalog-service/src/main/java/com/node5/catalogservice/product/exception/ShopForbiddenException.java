package com.node5.catalogservice.product.exception;

import com.node5.common.exception.BaseException;

public class ShopForbiddenException extends BaseException {
	public ShopForbiddenException() {
		super(ProductErrorCode.SHOP_FORBIDDEN);
	}
}
