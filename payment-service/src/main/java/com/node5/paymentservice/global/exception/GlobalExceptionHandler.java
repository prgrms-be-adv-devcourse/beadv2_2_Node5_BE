package com.node5.paymentservice.global.exception;

import com.node5.paymentservice.payment.exception.PaymentException;
import com.node5.common.exception.ExceptionResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ExceptionResponseDto> paymentHandleException(PaymentException e) {
        var ex = e.getErrorCode();
        ExceptionResponseDto responseDto = new ExceptionResponseDto( ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(responseDto);
    }

    // Bean Validation 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();
        ExceptionResponseDto responseDto = new ExceptionResponseDto("VALIDATION_ERROR", errorMessage);
        return ResponseEntity.status(BAD_REQUEST).body(responseDto);
    }
}
