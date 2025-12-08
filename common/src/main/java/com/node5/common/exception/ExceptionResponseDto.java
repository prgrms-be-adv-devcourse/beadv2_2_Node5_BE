package com.node5.common.exception;

public record ExceptionResponseDto(
        String code,
        String message
) {
}
