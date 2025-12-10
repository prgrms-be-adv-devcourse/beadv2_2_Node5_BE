package com.node5.common.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String code,
        String message,
        LocalDateTime timestamp,
        String path
) {

    public static ErrorResponse of(int status, String code, String message, String path) {
        return new ErrorResponse(status, code, message, LocalDateTime.now(), path);
    }
}