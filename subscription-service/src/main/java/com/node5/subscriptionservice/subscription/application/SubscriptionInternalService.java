package com.node5.subscriptionservice.subscription.application;

import com.node5.common.domain.PageInfoDto;
import com.node5.common.domain.PagedResponseDto;
import com.node5.common.event.SubscriptionOrderBatchChunkResultEvent;
import com.node5.subscriptionservice.subscription.application.dto.SubscriptionBatchTarget;
import com.node5.subscriptionservice.subscription.domain.Subscription;
import com.node5.subscriptionservice.subscription.domain.SubscriptionRecurrenceRule;
import com.node5.subscriptionservice.subscription.domain.SubscriptionRecurrenceRuleRepository;
import com.node5.subscriptionservice.subscription.domain.SubscriptionRepository;
import com.node5.subscriptionservice.subscription.domain.SubscriptionStatus;
import com.node5.subscriptionservice.subscription.exception.SubscriptionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static com.node5.subscriptionservice.subscription.exception.SubscriptionErrorCode.SUBSCRIPTION_RULE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionInternalService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionRecurrenceRuleRepository subscriptionRecurrenceRuleRepository;

    public PagedResponseDto<SubscriptionBatchTarget> findBatchTargets(LocalDate runDate, Pageable pageable) {
        Page<Subscription> page = subscriptionRepository
                .findAllByNextRunDateAndSubscriptionStatus(runDate, SubscriptionStatus.ACTIVE, pageable);

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

        List<UUID> successIds = results.stream()
                .filter(SubscriptionOrderBatchChunkResultEvent.SubscriptionOrderBatchResultItem::success)
                .map(result -> UUID.fromString(result.subscriptionId()))
                .toList();

        List<UUID> retryIds = results.stream()
                .filter(result -> !result.success() && result.retryable())
                .map(result -> UUID.fromString(result.subscriptionId()))
                .toList();

        List<UUID> failedIds = results.stream()
                .filter(result -> !result.success() && !result.retryable())
                .map(result -> UUID.fromString(result.subscriptionId()))
                .toList();

        results.stream()
                .filter(result -> !result.success())
                .forEach(result -> log.warn("Subscription batch failed: {} reason={}",
                        result.subscriptionId(), result.failureReason()));

        if (!successIds.isEmpty()) {
            applySuccessResults(successIds, runDate);
        }

        if (!retryIds.isEmpty()) {
            subscriptionRepository.bulkUpdateNextRunDateByIds(retryIds, runDate.plusDays(1));
            log.info("Subscription batch retry scheduled: {} items", retryIds.size());
        }

        if (!failedIds.isEmpty()) {
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
                subscription.getThumbnailUrl(),
                subscription.getPricePerItem(),
                subscription.getQuantity(),
                subscription.getTotalPrice()
        );
    }
}
