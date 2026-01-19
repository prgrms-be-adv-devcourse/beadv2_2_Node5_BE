package com.node5.paymentservice.payment.application.dto;

public record PaymentConfirmCommand(
        String paymentKey,
        String orderId,
        Long amount
) {
}
