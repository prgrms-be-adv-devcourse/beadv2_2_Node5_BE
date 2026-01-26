package com.node5.common.event;

import java.util.UUID;

public record PaymentSendEmailEvent(
        UUID memberId,
        String orderId,
        Long amount,
        String status,
        String failureReason
) {
}
