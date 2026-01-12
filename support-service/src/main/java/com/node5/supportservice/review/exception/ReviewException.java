package com.node5.supportservice.review.exception;

import com.node5.common.exception.BaseException;
import com.node5.supportservice.notification.exception.NotificationErrorCode;

public class ReviewException extends BaseException {
    public ReviewException(ReviewErrorCode errorCode) {
        super(errorCode);
    }
}
