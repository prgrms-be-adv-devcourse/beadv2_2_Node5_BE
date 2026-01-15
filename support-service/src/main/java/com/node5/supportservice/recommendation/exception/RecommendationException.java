package com.node5.supportservice.recommendation.exception;

import com.node5.common.exception.BaseErrorCode;
import com.node5.common.exception.BaseException;

public class RecommendationException extends BaseException {
    public RecommendationException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
