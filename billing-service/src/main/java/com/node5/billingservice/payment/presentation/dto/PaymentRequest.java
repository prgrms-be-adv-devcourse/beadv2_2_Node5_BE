package com.node5.billingservice.payment.presentation.dto;

import com.node5.billingservice.payment.application.dto.PaymentCommand;

public record PaymentRequest(
        Long amount
) {
    public PaymentCommand toCommand() {
        return new PaymentCommand(amount);
    }
}
