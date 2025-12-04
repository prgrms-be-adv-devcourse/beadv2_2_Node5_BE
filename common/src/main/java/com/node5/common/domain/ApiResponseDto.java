package com.node5.common.domain;

public record ApiResponseDto<T>(
        int status,
        String message,
        T data
) {
}
