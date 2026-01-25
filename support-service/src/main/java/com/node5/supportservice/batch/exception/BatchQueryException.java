package com.node5.supportservice.batch.exception;

import com.node5.common.exception.BaseErrorCode;
import com.node5.common.exception.BaseException;

public class BatchQueryException extends BaseException {
    public BatchQueryException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
