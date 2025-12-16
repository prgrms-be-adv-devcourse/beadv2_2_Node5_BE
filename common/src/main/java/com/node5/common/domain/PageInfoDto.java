package com.node5.common.domain;

public record PageInfoDto(
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages
) {
}
