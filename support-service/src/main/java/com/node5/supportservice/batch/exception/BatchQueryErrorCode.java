package com.node5.supportservice.batch.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BatchQueryErrorCode implements BaseErrorCode {
    JOB_EXECUTION_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "BATCH_QUERY_001", "해당 배치 실행 이력을 찾을 수 없습니다."),
    UNKNOWN_BATCH_JOB_NAME(HttpStatus.BAD_REQUEST.value(), "BATCH_QUERY_002", "알 수 없는 jobName입니다.");

    private final int status;
    private final String code;
    private final String message;
}
