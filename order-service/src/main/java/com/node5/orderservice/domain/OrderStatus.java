package com.node5.orderservice.domain;

public enum OrderStatus {
    CREATED, CANCELED,
    PAID, PAYMENT_FAILED,
    DELIVERY_ING, DELIVERY_COMPLETED,
    REFUND_REQUESTED, REFUND_COMPLETED
}
