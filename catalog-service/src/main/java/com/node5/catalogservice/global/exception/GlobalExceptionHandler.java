package com.node5.catalogservice.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.node5.common.exception.BaseErrorCode;
import com.node5.common.exception.BaseException;
import com.node5.common.exception.ExceptionResponseDto;

import jakarta.servlet.http.HttpServletRequest;

/**
 * BaseException 기반 도메인 예외와 validation 예외를 처리하는 전역 핸들러.
 * ErrorCode에 정의된 상태 코드와 메시지를 JSON 응답으로 반환합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 도메인 계층에서 발생하는 BaseException을 처리하여
	 * ErrorCode 기반의 일관된 에러 응답을 반환합니다.
	 */
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ExceptionResponseDto> handleBaseException(
		BaseException e,
		HttpServletRequest request
	) {
		BaseErrorCode errorCode = e.getErrorCode();

		ExceptionResponseDto body = new ExceptionResponseDto(
			errorCode.getCode(),
			errorCode.getMessage()
		);

		return ResponseEntity.status(errorCode.getStatus()).body(body);
	}

	/**
	 * @Valid 요청 본문 검증 실패(MethodArgumentNotValidException)를 처리합니다.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponseDto> handleValidationException(
		MethodArgumentNotValidException e
	) {
		String message = e.getBindingResult()
			.getAllErrors()
			.stream()
			.findFirst()
			.map(error -> error.getDefaultMessage())
			.orElse("잘못된 요청입니다.");

		ExceptionResponseDto body = new ExceptionResponseDto(
			"VALIDATION_ERROR", message
		);

		return ResponseEntity.badRequest().body(body);
	}
}
