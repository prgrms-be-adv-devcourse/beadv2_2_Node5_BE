package com.node5.memberservice.member.exception;

import com.node5.common.exception.BaseException;

public class MemberException extends BaseException {
    public MemberException(MemberErrorCode errorCode) {
        super(errorCode);
    }
}
