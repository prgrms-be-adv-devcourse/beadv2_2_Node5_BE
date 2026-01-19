package com.node5.paymentservice.payment.domain;

    public enum PaymentStatus {
        PENDING,
        PENDING_CONFIRM,
        CONFIRMED,
        PENDING_CANCEL,
        WITHDRAW_CONFIRMED,
        CANCELED,
        PAYMENT_FAILED,
        CANCEL_FAILED
    }
