package com.node5.paymentservice.payment.application.dto;

import com.node5.paymentservice.payment.domain.PaymentStatus;

public record PaymentFailureInfo(
        PaymentStatus paymentStatus
) {
    public static PaymentFailureInfo from(PaymentStatus status) {
        return new PaymentFailureInfo(status);
    }
}
