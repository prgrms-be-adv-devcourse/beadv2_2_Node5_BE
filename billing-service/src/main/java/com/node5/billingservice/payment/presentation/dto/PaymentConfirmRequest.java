package com.node5.billingservice.payment.presentation.dto;

import com.node5.billingservice.payment.application.dto.PaymentConfirmCommand;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderId,
        Long amount
) {
    public PaymentConfirmCommand toCommand() {
        return new PaymentConfirmCommand(
                paymentKey,
                orderId,
                amount
        );
    }
}
