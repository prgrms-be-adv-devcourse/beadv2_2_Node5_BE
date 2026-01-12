package com.node5.common.event;

public record OrderStatusChangedEvent(
        String orderId,
        String memberId,
        String orderStatus
) {
}
