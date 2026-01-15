package com.node5.supportservice.notification.exception;

import com.node5.common.exception.BaseException;

public class NotificationException extends BaseException {
    public NotificationException(NotificationErrorCode errorCode) {
        super(errorCode);
    }
}
