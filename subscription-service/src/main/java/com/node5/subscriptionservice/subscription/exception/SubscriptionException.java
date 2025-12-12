package com.node5.subscriptionservice.subscription.exception;

import com.node5.common.exception.BaseException;

public class SubscriptionException extends BaseException {
    public SubscriptionException(SubscriptionErrorCode subscriptionErrorCode) {
        super(subscriptionErrorCode);
    }
}
