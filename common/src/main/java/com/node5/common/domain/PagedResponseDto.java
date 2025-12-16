package com.node5.common.domain;

import java.util.List;

public record PagedResponseDto<T>(
        List<T> content,
        PageInfoDto pageInfo
) {
}
