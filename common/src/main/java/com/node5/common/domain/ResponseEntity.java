package com.node5.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseEntity<T> {
    private final int status;
    private final T data;
    private final long count;
}

