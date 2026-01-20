package com.node5.paymentservice.payment.application.dto;

public record PaymentRequestInfo(
        String orderId
) {
    public static PaymentRequestInfo from(String orderId) {
        return new PaymentRequestInfo(orderId);
    }
}
