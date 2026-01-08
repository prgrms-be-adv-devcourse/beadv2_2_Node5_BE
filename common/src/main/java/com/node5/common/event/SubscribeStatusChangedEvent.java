package com.node5.common.event;

public record SubscribeStatusChangedEvent(
        String subscribeId,
        String memberId,
        String subscribeStatus
) {
}
