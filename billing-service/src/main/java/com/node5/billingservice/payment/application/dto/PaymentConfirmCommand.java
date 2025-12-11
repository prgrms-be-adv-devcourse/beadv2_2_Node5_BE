package com.node5.billingservice.payment.application.dto;

public record PaymentConfirmCommand(
        String paymentKey,
        String orderId,
        Long amount
) {
}
