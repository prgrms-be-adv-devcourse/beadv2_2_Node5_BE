package com.node5.orderservice.subscription.application;

import com.node5.common.domain.PageInfoDto;
import com.node5.common.domain.PagedResponseDto;
import com.node5.common.event.SubscriptionOrderBatchChunkResultEvent;
import com.node5.common.event.SubscriptionOrderBatchResultType;
import com.node5.common.event.SubscriptionStatusChangedEvent;
import com.node5.orderservice.subscription.application.dto.SubscriptionBatchTarget;
import com.node5.orderservice.subscription.domain.Subscription;
import com.node5.orderservice.subscription.domain.SubscriptionRecurrenceRule;
import com.node5.orderservice.subscription.domain.SubscriptionRecurrenceRuleRepository;
import com.node5.orderservice.subscription.domain.SubscriptionRepository;
import com.node5.orderservice.subscription.domain.SubscriptionStatus;
import com.node5.orderservice.subscription.exception.SubscriptionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.node5.orderservice.subscription.exception.SubscriptionErrorCode.SUBSCRIPTION_RULE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionInternalService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionRecurrenceRuleRepository subscriptionRecurrenceRuleRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PagedResponseDto<SubscriptionBatchTarget> findBatchTargets(LocalDate runDate, Pageable pageable) {
        Page<Subscription> page = subscriptionRepository
                .findAllByNextRunDateAndSubscriptionStatusIn(
                        runDate,
                        List.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.FAILED),
                        pageable
                );

        List<SubscriptionBatchTarget> targets = page.getContent().stream()
                .map(this::toBatchTarget)
                .toList();

        PageInfoDto pageInfo = new PageInfoDto(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );

        return new PagedResponseDto<>(targets, pageInfo);
    }

    @Transactional
    public void applyBatchResult(SubscriptionOrderBatchChunkResultEvent event) {
        LocalDate runDate = LocalDate.parse(event.runDate());
        List<SubscriptionOrderBatchChunkResultEvent.SubscriptionOrderBatchResultItem> results = event.results();

        List<UUID> successIds = new java.util.ArrayList<>();
        List<UUID> paymentFailedIds = new java.util.ArrayList<>();
        List<UUID> unavailableIds = new java.util.ArrayList<>();
        List<UUID> retryIds = new java.util.ArrayList<>();
        List<UUID> failedIds = new java.util.ArrayList<>();

        for (var result : results) {
            UUID subscriptionId = UUID.fromString(result.subscriptionId());
            SubscriptionOrderBatchResultType resultType = result.resultType();

            if (resultType != SubscriptionOrderBatchResultType.SUCCESS) {
                log.warn("Subscription batch non-success result: {} type={}", result.subscriptionId(), resultType);
            }

            switch (resultType) {
                case SUCCESS -> successIds.add(subscriptionId);
                case PAYMENT_FAILED -> paymentFailedIds.add(subscriptionId);
                case UNAVAILABLE -> unavailableIds.add(subscriptionId);
                case RETRYABLE_FAILURE -> retryIds.add(subscriptionId);
                case NON_RETRYABLE_FAILURE -> failedIds.add(subscriptionId);
            }
        }

        if (!successIds.isEmpty()) {
            applySuccessResults(successIds, runDate);
            restoreFailedSubscriptions(successIds);
        }

        if (!retryIds.isEmpty()) {
            subscriptionRepository.bulkUpdateNextRunDateByIds(retryIds, runDate.plusDays(1));
            log.info("Subscription batch retry scheduled: {} items", retryIds.size());
        }

        if (!paymentFailedIds.isEmpty()) {
            publishStatusChanges(paymentFailedIds, SubscriptionStatus.FAILED);
            subscriptionRepository.bulkMarkFailedByIds(paymentFailedIds);
            subscriptionRepository.bulkUpdateNextRunDateByIds(paymentFailedIds, runDate.plusDays(1));
            log.info("Subscription batch payment failed: {} items", paymentFailedIds.size());
        }

        if (!unavailableIds.isEmpty()) {
            publishStatusChanges(unavailableIds, SubscriptionStatus.UNAVAILABLE);
            subscriptionRepository.bulkMarkUnavailableByIds(unavailableIds);
            log.info("Subscription batch marked unavailable: {} items", unavailableIds.size());
        }

        if (!failedIds.isEmpty()) {
            publishStatusChanges(failedIds, SubscriptionStatus.FAILED);
            subscriptionRepository.bulkMarkFailedByIds(failedIds);
            log.info("Subscription batch marked failed: {} items", failedIds.size());
        }
    }

    private void applySuccessResults(List<UUID> subscriptionIds, LocalDate runDate) {
        List<Subscription> subscriptions = subscriptionRepository.findAllById(subscriptionIds);
        if (subscriptions.isEmpty()) {
            return;
        }

        List<SubscriptionRecurrenceRule> rules =
                subscriptionRecurrenceRuleRepository.findAllBySubscriptionIdIn(subscriptionIds);
        if (rules.isEmpty()) {
            throw new SubscriptionException(SUBSCRIPTION_RULE_NOT_FOUND);
        }

        Map<UUID, List<SubscriptionRecurrenceRule>> ruleMap = rules.stream()
                .collect(Collectors.groupingBy(SubscriptionRecurrenceRule::getSubscriptionId));

        Map<UUID, LocalDate> nextRunDates = new HashMap<>();
        subscriptions.forEach(subscription -> {
            LocalDate lastProcessed = subscription.getLastProcessedRunDate();
            if (lastProcessed != null && !runDate.isAfter(lastProcessed)) {
                return;
            }
            List<SubscriptionRecurrenceRule> subscriptionRules =
                    ruleMap.getOrDefault(subscription.getId(), List.of());
            if (subscriptionRules.isEmpty()) {
                throw new SubscriptionException(SUBSCRIPTION_RULE_NOT_FOUND);
            }
            subscription.calculateNextRunDate(subscriptionRules, runDate);
            nextRunDates.put(subscription.getId(), subscription.getNextRunDate());
        });

        if (nextRunDates.isEmpty()) {
            log.info("Subscription batch success: no updates needed");
            return;
        }

        subscriptionRepository.bulkUpdateNextRunDates(nextRunDates, runDate);
        log.info("Subscription batch success: {} items updated", nextRunDates.size());
    }

    private SubscriptionBatchTarget toBatchTarget(Subscription subscription) {
        return new SubscriptionBatchTarget(
                subscription.getId(),
                subscription.getMemberId(),
                subscription.getDeliveryAddress(),
                subscription.getProductId(),
                subscription.getProductName(),
                subscription.getThumbnailKey(),
                subscription.getPricePerItem(),
                subscription.getQuantity(),
                subscription.getTotalPrice()
        );
    }

    private void restoreFailedSubscriptions(List<UUID> subscriptionIds) {
        List<Subscription> subscriptions = subscriptionRepository.findAllById(subscriptionIds);
        if (subscriptions.isEmpty()) {
            return;
        }

        List<UUID> failedIds = subscriptions.stream()
                .filter(subscription -> subscription.getSubscriptionStatus() == SubscriptionStatus.FAILED)
                .map(Subscription::getId)
                .toList();

        if (failedIds.isEmpty()) {
            return;
        }

        subscriptionRepository.bulkMarkActiveByIds(failedIds);
        publishStatusChanges(failedIds, SubscriptionStatus.ACTIVE);
        log.info("Subscription batch restored to active: {} items", failedIds.size());
    }

    private void publishStatusChanges(List<UUID> subscriptionIds, SubscriptionStatus status) {
        List<Subscription> subscriptions = subscriptionRepository.findAllById(subscriptionIds);
        if (subscriptions.isEmpty()) {
            return;
        }

        subscriptions.forEach(subscription -> eventPublisher.publishEvent(new SubscriptionStatusChangedEvent(
                subscription.getId().toString(),
                subscription.getMemberId().toString(),
                status.name()
        )));
    }
}
