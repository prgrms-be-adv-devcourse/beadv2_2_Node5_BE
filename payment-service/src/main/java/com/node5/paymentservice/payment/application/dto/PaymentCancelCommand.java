package com.node5.paymentservice.payment.application.dto;

public record PaymentCancelCommand(
        String paymentKey,
        String orderId,
        Long amount
) {
}
