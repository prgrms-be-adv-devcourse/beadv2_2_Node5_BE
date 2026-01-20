package com.node5.paymentservice.payment.application.dto;

import com.node5.paymentservice.payment.domain.Payment;
import com.node5.paymentservice.payment.domain.PaymentStatus;

import java.util.UUID;

public record PaymentInfo(
        UUID memberId,
        String paymentKey,
        String orderId,
        Long amount,
        String method,
        PaymentStatus status,
        String requestedAt,
        String approvedAt,
        String failReason
) {
    public static PaymentInfo from(Payment payment) {
        return new PaymentInfo(
                payment.getMemberId(),
                payment.getPaymentKey(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getRequestedAt() != null ? payment.getRequestedAt().toString() : null,
                payment.getApprovedAt() != null ? payment.getApprovedAt().toString() : null,
                payment.getFailReason()
        );
    }
}
