package com.node5.catalogservice.product.exception;

import com.node5.common.exception.BaseException;

public class ProductInvalidStockException extends BaseException {
	public ProductInvalidStockException(int stock) {
		super(ProductErrorCode.PRODUCT_INVALID_STOCK);
	}
}
