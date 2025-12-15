package com.node5.memberservice.inquiry.exception;

import com.node5.common.exception.BaseException;

public class InquiryException extends BaseException {
    public InquiryException(InquiryErrorCode errorCode) {
        super(errorCode);
    }
}
