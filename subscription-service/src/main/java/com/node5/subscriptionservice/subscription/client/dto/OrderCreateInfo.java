package com.node5.subscriptionservice.subscription.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreateInfo(
        UUID orderId
) {
}
