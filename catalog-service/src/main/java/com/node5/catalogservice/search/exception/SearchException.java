package com.node5.catalogservice.search.exception;

import com.node5.common.exception.BaseException;

public class SearchException extends BaseException {
	public SearchException(SearchErrorCode errorCode) {
		super(errorCode);
	}
}
