package com.node5.batchservice.subscription.batch.dto;

import com.node5.common.event.SubscriptionOrderBatchResultType;

import java.util.UUID;

public record SubscriptionBatchResult(
        UUID subscriptionId,
        SubscriptionOrderBatchResultType resultType,
        UUID orderId
) {
}
