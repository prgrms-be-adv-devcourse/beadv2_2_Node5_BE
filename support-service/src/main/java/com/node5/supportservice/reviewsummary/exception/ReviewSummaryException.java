package com.node5.supportservice.reviewsummary.exception;

import com.node5.common.exception.BaseErrorCode;
import com.node5.common.exception.BaseException;

public class ReviewSummaryException extends BaseException {
    public ReviewSummaryException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
