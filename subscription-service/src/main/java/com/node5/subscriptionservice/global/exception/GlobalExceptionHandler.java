package com.node5.subscriptionservice.global.exception;

import com.node5.common.exception.ExceptionResponseDto;
import com.node5.subscriptionservice.subscription.exception.SubscriptionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("유효성 검사 실패");

        ExceptionResponseDto responseDto = new ExceptionResponseDto("INVALID_INPUT", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ExceptionHandler(SubscriptionException.class)
    public ResponseEntity<ExceptionResponseDto> subscriptionHandleException(SubscriptionException e) {
        var ex = e.getErrorCode();
        ExceptionResponseDto responseDto = new ExceptionResponseDto( ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(responseDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleException(Exception e) {
        ExceptionResponseDto responseDto = new ExceptionResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
    }
}
