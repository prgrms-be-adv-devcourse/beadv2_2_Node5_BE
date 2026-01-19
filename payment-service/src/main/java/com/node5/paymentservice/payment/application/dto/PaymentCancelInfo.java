package com.node5.paymentservice.payment.application.dto;

import com.node5.paymentservice.payment.domain.PaymentStatus;

public record PaymentCancelInfo(
        PaymentStatus paymentStatus
) {
    public static PaymentCancelInfo from(PaymentStatus paymentStatus) {
        return new PaymentCancelInfo(paymentStatus);
    }
}
