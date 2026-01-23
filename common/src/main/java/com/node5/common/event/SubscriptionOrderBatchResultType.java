package com.node5.common.event;

public enum SubscriptionOrderBatchResultType {
    SUCCESS,
    PAYMENT_FAILED,
    UNAVAILABLE,
    RETRYABLE_FAILURE,
    NON_RETRYABLE_FAILURE
}
