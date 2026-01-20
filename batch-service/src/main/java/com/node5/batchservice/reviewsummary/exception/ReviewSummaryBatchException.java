package com.node5.batchservice.reviewsummary.exception;

import com.node5.common.exception.BaseErrorCode;
import com.node5.common.exception.BaseException;

public class ReviewSummaryBatchException extends BaseException {
    public ReviewSummaryBatchException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
