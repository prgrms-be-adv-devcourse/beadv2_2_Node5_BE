package com.node5.orderservice.subscription.infrastructure;

import com.node5.orderservice.subscription.domain.SubscriptionRecurrenceRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRecurrenceRuleJpaRepository extends JpaRepository<SubscriptionRecurrenceRule, UUID> {

    List<SubscriptionRecurrenceRule> findAllBySubscriptionId(UUID subscriptionId);

    List<SubscriptionRecurrenceRule> findAllBySubscriptionIdIn(List<UUID> subscriptionIds);

    void deleteAllBySubscriptionId(UUID subscriptionId);
}
