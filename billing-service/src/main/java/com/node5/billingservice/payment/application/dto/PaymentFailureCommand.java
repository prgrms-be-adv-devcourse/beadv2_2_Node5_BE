package com.node5.billingservice.payment.application.dto;

public record PaymentFailureCommand(
        String orderId,
        String errorCode,
        String errorMessage,
        Long amount,
        String rawPayload
) {
}
