package com.node5.billingservice.payment.application.dto;

import com.node5.billingservice.payment.domain.PaymentFailure;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentFailureInfo(
        UUID id,
        String orderId,
        String errorCode,
        String errorMessage,
        Long amount,
        LocalDateTime createdAt
) {
    public static PaymentFailureInfo from(PaymentFailure failure) {
        return new PaymentFailureInfo(
                failure.getId(),
                failure.getOrderId(),
                failure.getErrorCode(),
                failure.getErrorMessage(),
                failure.getAmount(),
                failure.getCreatedAt()
        );
    }
}
