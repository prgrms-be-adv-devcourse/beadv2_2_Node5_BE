package com.node5.supportservice.search.exception;

import com.node5.common.exception.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchErrorCode implements BaseErrorCode {

	INVALID_PRICE_RANGE(400, "SEARCH_INVALID_PRICE_RANGE", "가격 범위가 올바르지 않습니다."),
	PRICE_RANGE_INCOMPLETE(400, "SEARCH_PRICE_RANGE_INCOMPLETE", "최소/최대 가격은 함께 입력해야 합니다.");

	private final int status;
	private final String code;
	private final String message;
}
