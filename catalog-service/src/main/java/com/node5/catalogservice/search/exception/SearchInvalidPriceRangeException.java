package com.node5.catalogservice.search.exception;

import com.node5.common.exception.BaseException;

public class SearchInvalidPriceRangeException extends BaseException {
	public SearchInvalidPriceRangeException() {
		super(SearchErrorCode.INVALID_PRICE_RANGE);
	}
}
