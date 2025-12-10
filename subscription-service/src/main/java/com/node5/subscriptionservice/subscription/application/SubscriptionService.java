package com.node5.subscriptionservice.subscription.application;

import com.node5.common.domain.ApiResponseDto;
import com.node5.common.domain.PageInfoDto;
import com.node5.common.domain.PagedResponseDto;
import com.node5.subscriptionservice.subscription.application.dto.SubscriptionCreateCommand;
import com.node5.subscriptionservice.subscription.application.dto.SubscriptionInfo;
import com.node5.subscriptionservice.subscription.application.dto.SubscriptionUpdateCommand;
import com.node5.subscriptionservice.subscription.domain.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionRecurrenceRuleRepository subscriptionRecurrenceRuleRepository;

    public SubscriptionInfo findById(UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found: " + id));
        return toSubscriptionInfo(subscription);
    }

    public Page<SubscriptionInfo> findAllByMemberId(UUID memberId, Pageable pageable) {
        Page<Subscription> subscriptions = subscriptionRepository.findAllByMemberId(memberId, pageable);
        return subscriptions.map(this::toSubscriptionInfo);
    }

    @Transactional
    public SubscriptionInfo create(SubscriptionCreateCommand command) {
        Subscription subscription = Subscription.create(
                command.memberId(),
                command.productId(),
                command.pricePerItem(),
                command.quantity(),
                command.deliveryAddress()
        );

        List<SubscriptionRecurrenceRule> rules = createSubscriptionRecurrenceRule(
                subscription.getId(),
                command.recurrenceType(),
                command.dayOfWeek(),
                command.dayOfMonth()
        );

        if (rules.isEmpty()) {
            throw new IllegalArgumentException("No recurrence rules created");
        }

        subscription.calculateNextRunDate(rules);

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        subscriptionRecurrenceRuleRepository.saveAll(rules);

        return toSubscriptionInfo(savedSubscription);
    }

    @Transactional
    public SubscriptionInfo update(SubscriptionUpdateCommand command, UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found: " + id));

        subscription.update(
                command.pricePerItem(),
                command.quantity(),
                command.deliveryAddress()
        );

        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        if(command.recurrenceType() != null) {

            List<SubscriptionRecurrenceRule> newRules = createSubscriptionRecurrenceRule(
                    id,
                    command.recurrenceType(),
                    command.dayOfWeek(),
                    command.dayOfMonth()
            );

            subscriptionRecurrenceRuleRepository.deleteAllBySubscriptionId(id);
            subscription.calculateNextRunDate(newRules);
            subscriptionRecurrenceRuleRepository.saveAll(newRules);
        }

        return toSubscriptionInfo(updatedSubscription);
    }

    @Transactional
    public SubscriptionInfo pause(UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found: " + id));

        subscription.pause();
        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        return toSubscriptionInfo(updatedSubscription);
    }

    @Transactional
    public SubscriptionInfo resume(UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found: " + id));

        subscription.resume();
        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        return toSubscriptionInfo(updatedSubscription);
    }

    @Transactional
    public SubscriptionInfo delete(UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subscription not found: " + id));

        subscription.delete();
        Subscription saved = subscriptionRepository.save(subscription);

        return toSubscriptionInfo(saved);
    }

    private List<SubscriptionRecurrenceRule> createSubscriptionRecurrenceRule(UUID subscriptionId, RecurrenceType recurrenceType, List<DayOfWeek> dayOfWeek, Integer dayOfMonth) {
        if (recurrenceType == RecurrenceType.WEEKLY) {
            if (dayOfWeek == null || dayOfWeek.isEmpty()) {
                throw new IllegalArgumentException("DayOfWeek not included");
            }

            return dayOfWeek.stream()
                    .map(day -> SubscriptionRecurrenceRule.create(
                            subscriptionId,
                            recurrenceType,
                            day,
                            dayOfMonth))
                    .toList();
        }  else if (recurrenceType ==  RecurrenceType.MONTHLY) {
            if (dayOfMonth == null) {
                throw new IllegalArgumentException("dayOfMonth not included");
            }
            if (dayOfMonth < 1 || dayOfMonth > 31) {
                throw new IllegalArgumentException("dayOfMonth must be between 1 and 31");
            }
            return List.of(SubscriptionRecurrenceRule.create(
                    subscriptionId,
                    recurrenceType,
                    null,
                    dayOfMonth)
            );
        }
        return List.of();
    }

    private SubscriptionInfo toSubscriptionInfo(Subscription subscription) {
        List<SubscriptionRecurrenceRule> rules =
                subscriptionRecurrenceRuleRepository.findBySubscriptionId(subscription.getId());

        if (rules.isEmpty()) {
            throw new EntityNotFoundException("Recurrence rules not found for subscription: " + subscription.getId());
        }

        RecurrenceType ruleType = rules.get(0).getRecurrenceType();
        List<DayOfWeek> dayOfWeek = rules.stream()
                .map(SubscriptionRecurrenceRule::getDayOfWeek)
                .filter(Objects::nonNull)
                .toList();
        Integer dayOfMonth = rules.get(0).getDayOfMonth();

        return SubscriptionInfo.of(subscription, ruleType, dayOfWeek, dayOfMonth);
    }
}
