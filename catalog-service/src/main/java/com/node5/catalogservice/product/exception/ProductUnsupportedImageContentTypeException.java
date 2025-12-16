package com.node5.catalogservice.product.exception;

import com.node5.common.exception.BaseException;

public class ProductUnsupportedImageContentTypeException extends BaseException {

	public ProductUnsupportedImageContentTypeException() {
		super(ProductErrorCode.UNSUPPORTED_IMAGE_CONTENT_TYPE);
	}
}
