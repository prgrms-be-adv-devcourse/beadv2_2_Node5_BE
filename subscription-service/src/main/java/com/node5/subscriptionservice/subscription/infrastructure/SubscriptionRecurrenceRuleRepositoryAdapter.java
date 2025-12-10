package com.node5.subscriptionservice.subscription.infrastructure;

import com.node5.subscriptionservice.subscription.domain.SubscriptionRecurrenceRule;
import com.node5.subscriptionservice.subscription.domain.SubscriptionRecurrenceRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SubscriptionRecurrenceRuleRepositoryAdapter implements SubscriptionRecurrenceRuleRepository {

    private final SubscriptionRecurrenceRuleJpaRepository jpaRepository;

    @Override
    public List<SubscriptionRecurrenceRule> findBySubscriptionId(UUID subscriptionId){return jpaRepository.findBySubscriptionId(subscriptionId);}

    @Override
    public List<SubscriptionRecurrenceRule> saveAll(List<SubscriptionRecurrenceRule> rules){return jpaRepository.saveAll(rules);}

    @Override
    public void deleteAllBySubscriptionId(UUID subscriptionId){jpaRepository.deleteAllBySubscriptionId(subscriptionId);}
}
