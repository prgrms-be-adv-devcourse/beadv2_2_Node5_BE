package com.node5.orderservice.subscription.domain;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRecurrenceRuleRepository {
    List<SubscriptionRecurrenceRule> findAllBySubscriptionId(UUID subscriptionId);

    List<SubscriptionRecurrenceRule> findAllBySubscriptionIdIn(List<UUID> subscriptionIds);

    List<SubscriptionRecurrenceRule> saveAll(List<SubscriptionRecurrenceRule> rules);

    void deleteAllBySubscriptionId(UUID subscriptionId);
}
