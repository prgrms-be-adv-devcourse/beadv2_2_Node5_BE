package com.node5.orderservice.global.exception;

import com.node5.common.exception.ExceptionResponseDto;
import com.node5.orderservice.order.exception.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponseDto(OrderErrorCode.INVALID_VALUE.getCode(), message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponseDto> handleValidation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(violation -> {
                    String propertyPath = violation.getPropertyPath().toString();
                    String field = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
                    return field + ": " + violation.getMessage();
                })
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponseDto(OrderErrorCode.INVALID_VALUE.getCode(), message));
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ExceptionResponseDto> handleOrderException(OrderException e) {
        var ex = e.getErrorCode();

        String baseMessage = ex.getMessage();
        String finalMessage = baseMessage;

        if (e.getCustomMessage() != null && !e.getCustomMessage().trim().isEmpty()) {
            finalMessage = baseMessage + ": " + e.getCustomMessage();
        }

        ExceptionResponseDto responseDto = new ExceptionResponseDto(ex.getCode(), finalMessage);
        return ResponseEntity
                .status(ex.getStatus())
                .body(responseDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleException(Exception e) {
        ExceptionResponseDto responseDto = new ExceptionResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseDto);
    }

}
