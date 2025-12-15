package com.node5.catalogservice.product.exception;

import com.node5.common.exception.BaseException;

public class ProductNotFoundException extends BaseException {
	public ProductNotFoundException() {
		super(ProductErrorCode.PRODUCT_NOT_FOUND);
	}
}
