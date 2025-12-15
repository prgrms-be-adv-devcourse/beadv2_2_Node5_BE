package com.node5.catalogservice.product.exception;

import com.node5.common.exception.BaseException;

public class ProductStatusChangeNotAllowedException extends BaseException {

	public ProductStatusChangeNotAllowedException() {
		super(ProductErrorCode.PRODUCT_STATUS_CHANGE_NOT_ALLOWED);
	}
}
