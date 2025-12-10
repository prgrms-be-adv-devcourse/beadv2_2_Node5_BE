package com.node5.billingservice.global.exception;

import com.node5.billingservice.wallet.exception.WalletException;
import com.node5.common.exception.ExceptionResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WalletException.class)
    public ResponseEntity<ExceptionResponseDto> handleException(WalletException e) {
        var ex = e.getErrorCode();
        ExceptionResponseDto responseDto = new ExceptionResponseDto( ex.getCode(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(responseDto);
    }
}
