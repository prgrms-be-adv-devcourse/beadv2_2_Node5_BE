package com.node5.billingservice.payment.presentation.dto;

import com.node5.billingservice.payment.application.dto.PaymentFailureCommand;

public record PaymentFailureRequest(
        String orderId,
        String paymentKey,
        String code,
        String message,
        Long amount,
        String rawPayload
) {
    public PaymentFailureCommand toCommand() {
        return new PaymentFailureCommand(orderId, paymentKey, code, message, amount, rawPayload);
    }
}
