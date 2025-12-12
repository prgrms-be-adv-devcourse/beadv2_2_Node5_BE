package com.node5.catalogservice.product.exception;

import com.node5.common.exception.BaseException;

public class OnSaleProductNotFoundException extends BaseException {
	public OnSaleProductNotFoundException() {
		super(ProductErrorCode.ON_SALE_PRODUCT_NOT_FOUND);
	}
}
