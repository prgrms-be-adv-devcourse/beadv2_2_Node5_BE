package com.node5.common.event;

import java.util.UUID;

public record PaymentDepositEvent(
        UUID memberId,
        String orderId,
        Long amount
) {
}
