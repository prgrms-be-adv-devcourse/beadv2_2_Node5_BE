package com.node5.memberservice.endpoint.exception;

import com.node5.common.exception.BaseException;

public class EndPointException extends BaseException {
    public EndPointException(EndPointErrorCode errorCode) {
        super(errorCode);
    }
}
