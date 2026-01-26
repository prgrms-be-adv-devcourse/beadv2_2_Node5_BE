package com.node5.common.event;

import java.util.List;

public record SubscriptionOrderBatchChunkResultEvent(
        String runDate,
        List<SubscriptionOrderBatchResultItem> results
) {
    public record SubscriptionOrderBatchResultItem(
            String subscriptionId,
            SubscriptionOrderBatchResultType resultType,
            String orderId
    ) {
    }
}
