package com.node5.common.event;

public record SubscriptionStatusChangedEvent(
        String subscriptionId,
        String memberId,
        String subscriptionStatus
) {
}
