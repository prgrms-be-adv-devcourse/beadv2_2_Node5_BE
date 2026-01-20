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
	UNSUPPORTED_IMAGE_CONTENT_TYPE(415, "UNSUPPORTED_IMAGE_CONTENT_TYPE", "지원하지 않는 이미지 타입입니다."),
	S3_ACCESS_DENIED(500, "S3_ACCESS_DENIED", "이미지 저장소 접근이 거부되었습니다. 업로드 상태 또는 권한 설정을 확인해주세요."),
	S3_OPERATION_FAILED(500, "S3_OPERATION_FAILED", "이미지 저장소 처리 중 오류가 발생했습니다.");

	private final int status;
	private final String code;
	private final String message;
}
