package com.node5.subscriptionservice.subscription.client.dto;

import java.util.UUID;

public record OrderCreateInfo(
        UUID orderId
) {
}
