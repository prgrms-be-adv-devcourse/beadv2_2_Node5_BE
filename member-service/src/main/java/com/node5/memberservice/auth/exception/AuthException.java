package com.node5.memberservice.auth.exception;

import com.node5.common.exception.BaseException;

public class AuthException extends BaseException {
    public AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }
}
