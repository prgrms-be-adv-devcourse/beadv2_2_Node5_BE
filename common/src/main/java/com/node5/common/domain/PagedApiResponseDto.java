package com.node5.common.domain;

import java.util.List;

public record PagedApiResponseDto<T>(
        int status,
        String message,
        List<T> data,
        PageInfoDto pageInfo
) {
}
