package com.node5.subscriptionservice.subscription.infrastructure;

import com.node5.subscriptionservice.subscription.domain.SubscriptionRecurrenceRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRecurrenceRuleJpaRepository extends JpaRepository<SubscriptionRecurrenceRule, UUID> {

    List<SubscriptionRecurrenceRule> findBySubscriptionId(UUID subscriptionId);

    void deleteAllBySubscriptionId(UUID subscriptionId);
}
