package com.node5.billingservice.payment.presentation.dto;

import com.node5.billingservice.payment.application.dto.PaymentCancelCommand;

public record PaymentCancelRequest(String paymentKey,
                                   String orderId,
                                   Long amount
) {
    public PaymentCancelCommand toCommand() {
        return new PaymentCancelCommand(
                paymentKey,
                orderId,
                amount
        );
    }
}
