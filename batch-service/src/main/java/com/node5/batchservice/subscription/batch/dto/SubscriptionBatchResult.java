package com.node5.batchservice.subscription.batch.dto;

import java.util.UUID;

public record SubscriptionBatchResult(
        UUID subscriptionId,
        boolean success,
        UUID orderId,
        String failureReason,
        boolean retryable
) {
}
