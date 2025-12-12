package com.node5.catalogservice.search.exception;

import com.node5.common.exception.BaseException;

public class SearchPriceRangeIncompleteException extends BaseException {
	public SearchPriceRangeIncompleteException() {
		super(SearchErrorCode.PRICE_RANGE_INCOMPLETE);
	}
}
