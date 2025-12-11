package com.node5.billingservice.payment.presentation.dto;

import com.node5.billingservice.payment.application.dto.PaymentCommand;
import jakarta.validation.constraints.Positive;

public record PaymentRequest(
        @Positive(message = "결제 금액은 0보다 커야 합니다.")
        Long amount
) {
    public PaymentCommand toCommand() {
        return new PaymentCommand(amount);
    }
}
