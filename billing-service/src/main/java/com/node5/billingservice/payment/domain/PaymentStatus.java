package com.node5.billingservice.payment.domain;

public enum PaymentStatus {
    READY,
    CONFIRMED,
    FAILED,
    CANCEL_WAITING,
    CANCELED
}
