package com.node5.memberservice.endpoint.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum EndPointErrorCode implements BaseErrorCode {
    ENDPOINT_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "ENDPOINT_001", "존재하지 않은 EndPoint 입니다."),
    UNCAUGHT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ENDPOINT_002", "알 수 없는 서버에러");

    private final int status;
    private final String code;
    private final String message;

    EndPointErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
