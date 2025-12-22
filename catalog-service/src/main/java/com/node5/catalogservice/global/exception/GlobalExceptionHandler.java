package com.node5.catalogservice.global.exception;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.node5.common.exception.BaseErrorCode;
import com.node5.common.exception.BaseException;
import com.node5.common.exception.ExceptionResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ExceptionResponseDto> handleBaseException(BaseException e) {
		BaseErrorCode errorCode = e.getErrorCode();

		return ResponseEntity.status(errorCode.getStatus()).body(
			new ExceptionResponseDto(errorCode.getCode(), errorCode.getMessage())
		);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponseDto> handleValidationException(
		MethodArgumentNotValidException e
	) {
		String message = e.getBindingResult()
			.getFieldErrors()
			.stream()
			.findFirst()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.orElse("요청 값이 유효하지 않습니다.");

		return ResponseEntity.badRequest().body(
			new ExceptionResponseDto("VALIDATION_ERROR", message)
		);
	}

	@ExceptionHandler(ConversionFailedException.class)
	public ResponseEntity<ExceptionResponseDto> handleConversionFailed(
		ConversionFailedException e
	) {
		return ResponseEntity.badRequest().body(
			new ExceptionResponseDto(
				"TYPE_MISMATCH",
				"요청 파라미터 형식이 올바르지 않습니다."
			)
		);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ExceptionResponseDto> handleNotReadable(
		HttpMessageNotReadableException e
	) {
		return ResponseEntity.badRequest().body(
			new ExceptionResponseDto(
				"INVALID_REQUEST_BODY",
				"요청 본문 형식이 올바르지 않습니다."
			)
		);
	}
}
