package com.node5.catalogservice.client.exception;

import com.node5.common.exception.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClientErrorCode implements BaseErrorCode {

	SHOP_NOT_FOUND(404, "SHOP_NOT_FOUND", "해당 상점을 찾을 수 없습니다."),
	SHOP_SERVICE_UNAVAILABLE(503, "SHOP_SERVICE_UNAVAILABLE", "상점 소유자 정보를 조회할 수 없습니다.");

	private final int status;
	private final String code;
	private final String message;
}
