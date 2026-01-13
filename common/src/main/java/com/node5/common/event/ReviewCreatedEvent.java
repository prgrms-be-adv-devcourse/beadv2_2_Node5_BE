package com.node5.common.event;

import java.util.UUID;

public record ReviewCreatedEvent(
        UUID reviewId,
        String body
) {
}
