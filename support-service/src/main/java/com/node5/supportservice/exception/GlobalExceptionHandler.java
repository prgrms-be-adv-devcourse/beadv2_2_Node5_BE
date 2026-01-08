package com.node5.supportservice.exception;

import com.node5.common.exception.BaseErrorCode;
import com.node5.common.exception.BaseException;
import com.node5.common.exception.ExceptionResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionResponseDto> handleBaseException(BaseException e) {
        BaseErrorCode errorCode = e.getErrorCode();
        ExceptionResponseDto responseDto = new ExceptionResponseDto(errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(responseDto);
    }

}
