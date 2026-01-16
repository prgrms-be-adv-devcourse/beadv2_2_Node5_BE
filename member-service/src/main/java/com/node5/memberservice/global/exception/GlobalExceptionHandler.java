package com.node5.memberservice.global.exception;

import com.node5.common.exception.BaseErrorCode;
import com.node5.common.exception.BaseException;
import com.node5.common.exception.ExceptionResponseDto;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionResponseDto> handleBaseException(BaseException e) {
        BaseErrorCode errorCode = e.getErrorCode();
        ExceptionResponseDto responseDto = new ExceptionResponseDto(errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(responseDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("잘못된 요청입니다.");

        ExceptionResponseDto responseDto = new ExceptionResponseDto("VALIDATION_ERROR", errorMessage);

        return ResponseEntity.badRequest().body(responseDto);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> handleMissingHeader(MissingRequestHeaderException e) {
        String errorMessage = e.getHeaderName() + " 헤더가 누락되었습니다.";
        ExceptionResponseDto responseDto = new ExceptionResponseDto("MISSING_HEADER", errorMessage);
        return ResponseEntity.badRequest().body(responseDto);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String errorMessage = "잘못된 형식의 값입니다: " + e.getName();
        ExceptionResponseDto responseDto = new ExceptionResponseDto("TYPE_MISMATCH", errorMessage);
        return ResponseEntity.badRequest().body(responseDto);
    }

}
