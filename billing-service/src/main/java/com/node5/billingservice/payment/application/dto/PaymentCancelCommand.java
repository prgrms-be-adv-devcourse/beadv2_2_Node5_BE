package com.node5.billingservice.payment.application.dto;

public record PaymentCancelCommand(
        String paymentKey,
        String orderId,
        Long amount
) {
}
