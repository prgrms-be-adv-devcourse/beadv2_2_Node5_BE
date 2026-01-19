package com.node5.paymentservice.payment.application.dto;

import com.node5.paymentservice.payment.domain.PaymentStatus;

public record PaymentConfirmInfo(
        PaymentStatus paymentStatus
) {
    public static PaymentConfirmInfo from(PaymentStatus paymentStatus) {
        return new PaymentConfirmInfo(paymentStatus);
    }
}
