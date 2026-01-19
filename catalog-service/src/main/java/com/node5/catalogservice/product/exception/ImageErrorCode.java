package com.node5.catalogservice.product.exception;

import com.node5.common.exception.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements BaseErrorCode {

	INVALID_IMAGE_KEY(400, "INVALID_IMAGE_KEY", "유효하지 않은 이미지 키입니다."),
	IMAGE_NOT_FOUND(404, "IMAGE_NOT_FOUND", "이미지를 찾을 수 없습니다."),
	IMAGE_TOO_LARGE(413, "IMAGE_TOO_LARGE", "이미지 파일 크기가 허용 범위를 초과했습니다."),
	INVALID_IMAGE_FILE(400, "INVALID_IMAGE_FILE", "유효하지 않은 이미지 파일입니다."),
	UNSUPPORTED_IMAGE_CONTENT_TYPE(415, "UNSUPPORTED_IMAGE_CONTENT_TYPE", "지원하지 않는 이미지 타입입니다.");

	private final int status;
	private final String code;
	private final String message;
}
