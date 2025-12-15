package com.node5.settlementservice.global.exception;

import com.node5.common.exception.ExceptionResponseDto;
import com.node5.settlementservice.settlement.exception.SettlementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static com.node5.settlementservice.settlement.exception.SettlementErrorCode.INVALID_VALUE;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponseDto> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String baseMessage = INVALID_VALUE.getMessage() + ": ";

        if (ex.getName().equals("startDate") || ex.getName().equals("endDate")) {
            String message = String.format("'%s' 파라미터는 yyyy-MM 형식이어야 합니다. (입력값: '%s')",
                    ex.getName(), ex.getValue());
            ExceptionResponseDto responseDto = new ExceptionResponseDto(INVALID_VALUE.getCode(), baseMessage + message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }

        ExceptionResponseDto responseDto = new ExceptionResponseDto(INVALID_VALUE.getCode(), baseMessage + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ExceptionHandler(SettlementException.class)
    public ResponseEntity<ExceptionResponseDto> handleSettlementException(SettlementException e) {
        var ex = e.getErrorCode();

        String baseMessage = ex.getMessage();
        String finalMessage = baseMessage;

        if (e.getCustomMessage() != null && !e.getCustomMessage().trim().isEmpty()) {
            finalMessage = baseMessage + ": " + e.getCustomMessage();
        }

        ExceptionResponseDto responseDto = new ExceptionResponseDto(ex.getCode(), finalMessage);
        return ResponseEntity.status(ex.getStatus()).body(responseDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleException(Exception e) {
        ExceptionResponseDto responseDto = new ExceptionResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
    }
}
