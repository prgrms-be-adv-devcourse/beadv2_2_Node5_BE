package com.node5.subscriptionservice.subscription.application;

import com.node5.subscriptionservice.subscription.application.dto.SubscriptionCreateCommand;
import com.node5.subscriptionservice.subscription.application.dto.SubscriptionInfo;
import com.node5.subscriptionservice.subscription.application.dto.SubscriptionUpdateCommand;
import com.node5.subscriptionservice.subscription.client.ProductClient;
import com.node5.subscriptionservice.subscription.client.ShopClient;
import com.node5.subscriptionservice.subscription.client.dto.ProductInfoResponse;
import com.node5.subscriptionservice.subscription.domain.*;
import com.node5.subscriptionservice.subscription.exception.SubscriptionException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.node5.subscriptionservice.subscription.exception.SubscriptionErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionRecurrenceRuleRepository subscriptionRecurrenceRuleRepository;
    private final ProductClient productClient;
    private final ShopClient shopClient;

    public SubscriptionInfo findById(UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new SubscriptionException(SUBSCRIPTION_NOT_FOUND));
        return toSubscriptionInfo(subscription);
    }

    public Page<SubscriptionInfo> findAllByMemberId(UUID memberId, Pageable pageable) {
        Page<Subscription> subscriptions = subscriptionRepository.findAllByMemberId(memberId, pageable);
        Map<UUID, List<SubscriptionRecurrenceRule>> rules = findRulesBySubscriptionId(subscriptions.getContent());
        return subscriptions.map(subscription ->
                toSubscriptionInfo(subscription, rules.getOrDefault(subscription.getId(), List.of()))
        );
    }

    @Transactional
    public SubscriptionInfo create(SubscriptionCreateCommand command, UUID memberId) {
        log.info("Create subscription request: {}", command);
        ProductInfoResponse productInfo = getProductInfo(command.productId());
        validateProductNotCurrentMembersShop(memberId, productInfo.shopId());

        Subscription subscription = Subscription.create(
                memberId,
                productInfo.id(),
                productInfo.shopId(),
                productInfo.name(),
                productInfo.thumbnailUrl(),
                productInfo.price(),
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
            throw new SubscriptionException(SUBSCRIPTION_RULE_NOT_FOUND);
        }

        subscription.calculateNextRunDate(rules, LocalDate.now());

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        subscriptionRecurrenceRuleRepository.saveAll(rules);

        return toSubscriptionInfo(savedSubscription);
    }

    @Transactional
    public SubscriptionInfo update(SubscriptionUpdateCommand command, UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new SubscriptionException(SUBSCRIPTION_NOT_FOUND));

        List<SubscriptionRecurrenceRule> rule = subscriptionRecurrenceRuleRepository.findAllBySubscriptionId(subscription.getId());
        RecurrenceType type = rule.get(0).getRecurrenceType();

        if (type == RecurrenceType.WEEKLY) {
            // dayOfMonth 필드가 들어오면 에러
            if (command.dayOfMonth() != null) {
                throw new SubscriptionException(SUBSCRIPTION_RECURRENCE_UPDATE_NOT_ALLOWED);
            }
            // dayOfWeek 필드가 null이면 유지, 값 있으면 변경
            if (command.dayOfWeek() != null) {
                updateSubscriptionRecurrenceRule(subscription, RecurrenceType.WEEKLY, command.dayOfWeek(), command.dayOfMonth());
            }
        }

        if (type == RecurrenceType.MONTHLY) {
            // dayOfWeek 필드가 들어오면 에러
            if (command.dayOfWeek() != null) {
                throw new SubscriptionException(SUBSCRIPTION_RECURRENCE_UPDATE_NOT_ALLOWED);
            }
            // dayOfMonth 필드가 null이면 유지, 값 있으면 변경
            if (command.dayOfMonth() != null) {
                updateSubscriptionRecurrenceRule(subscription, RecurrenceType.MONTHLY, command.dayOfWeek(), command.dayOfMonth());
            }
        }

        subscription.update(command.deliveryAddress());

        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        return toSubscriptionInfo(updatedSubscription);
    }

    @Transactional
    public SubscriptionInfo pause(UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new SubscriptionException(SUBSCRIPTION_NOT_FOUND));

        subscription.pause();
        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        return toSubscriptionInfo(updatedSubscription);
    }

    @Transactional
    public SubscriptionInfo resume(UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new SubscriptionException(SUBSCRIPTION_NOT_FOUND));

        subscription.resume();
        Subscription updatedSubscription = subscriptionRepository.save(subscription);

        return toSubscriptionInfo(updatedSubscription);
    }

    @Transactional
    public SubscriptionInfo cancel(UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new SubscriptionException(SUBSCRIPTION_NOT_FOUND));

        subscription.cancel();
        Subscription saved = subscriptionRepository.save(subscription);

        return toSubscriptionInfo(saved);
    }

    public Page<SubscriptionInfo> findAllByProductId(UUID productId, Pageable pageable) {
        Page<Subscription> subscriptions = subscriptionRepository.findAllByProductId(productId, pageable);
        Map<UUID, List<SubscriptionRecurrenceRule>> rules = findRulesBySubscriptionId(subscriptions.getContent());
        return subscriptions.map(subscription ->
                toSubscriptionInfo(subscription, rules.getOrDefault(subscription.getId(), List.of()))
        );
    }

    @Transactional
    public void terminateUserSubscriptions(UUID memberId, List<UUID> shopIds) {
        List<Subscription> subscriptions = subscriptionRepository.findAllByMemberId(memberId);
        subscriptions.forEach(Subscription::terminate);
        shopIds.forEach(shopId -> {terminateSellerSubscriptions(shopId);});
    }

    @Transactional
    public void terminateSellerSubscriptions(UUID shopId) {
        // 대량 처리 고려하여 bulk update
        subscriptionRepository.bulkTerminateAllByShop(shopId, LocalDateTime.now());
    }

    private ProductInfoResponse getProductInfo(UUID productId) {
        try {
            ProductInfoResponse response = productClient.findById(productId).getBody();
            if (response == null) {
                throw new SubscriptionException(SUBSCRIPTION_PRODUCT_NOT_FOUND);
            }
            if (response.id() == null || response.name() == null || response.price() == null || response.shopId() == null) {
                throw new SubscriptionException(SUBSCRIPTION_PRODUCT_REQUEST_FAILED);
            }
            return response;
        } catch (SubscriptionException exception) {
            throw exception;
        } catch (FeignException.NotFound ex) {
            throw new SubscriptionException(SUBSCRIPTION_PRODUCT_NOT_FOUND);
        } catch (Exception ex) {
            log.info("{}", ex.getMessage());
            throw new SubscriptionException(SUBSCRIPTION_PRODUCT_REQUEST_FAILED);
        }
    }

    private void validateProductNotCurrentMembersShop(UUID memberId, UUID shopId) {
        UUID shopOwnerId;
        try {
            shopOwnerId = shopClient.getMemberIdByShopId(shopId).getBody();
        } catch (FeignException e) {
            throw new SubscriptionException(SUBSCRIPTION_SHOP_REQUEST_FAILED);
        }

        if(shopOwnerId.equals(memberId)) {
            throw new SubscriptionException(SUBSCRIPTION_SELF_PRODUCT_NOT_ALLOWED);
        }
    }

    private List<SubscriptionRecurrenceRule> createSubscriptionRecurrenceRule(UUID subscriptionId, RecurrenceType recurrenceType, List<DayOfWeek> dayOfWeek, Integer dayOfMonth) {
        if (recurrenceType == RecurrenceType.WEEKLY) {
            return dayOfWeek.stream()
                    .map(day -> SubscriptionRecurrenceRule.create(
                            subscriptionId,
                            recurrenceType,
                            day,
                            dayOfMonth))
                    .toList();
        }  else if (recurrenceType ==  RecurrenceType.MONTHLY) {
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
                subscriptionRecurrenceRuleRepository.findAllBySubscriptionId(subscription.getId());

        if (rules.isEmpty()) {
            throw new SubscriptionException(SUBSCRIPTION_RULE_NOT_FOUND);
        }

        RecurrenceType ruleType = rules.get(0).getRecurrenceType();
        List<DayOfWeek> dayOfWeek = rules.stream()
                .map(SubscriptionRecurrenceRule::getDayOfWeek)
                .filter(Objects::nonNull)
                .toList();
        Integer dayOfMonth = rules.get(0).getDayOfMonth();

        return SubscriptionInfo.of(subscription, ruleType, dayOfWeek, dayOfMonth);
    }

    private SubscriptionInfo toSubscriptionInfo(Subscription subscription, List<SubscriptionRecurrenceRule> rules) {
        if (rules.isEmpty()) {
            throw new SubscriptionException(SUBSCRIPTION_RULE_NOT_FOUND);
        }

        RecurrenceType ruleType = rules.get(0).getRecurrenceType();
        List<DayOfWeek> dayOfWeek = rules.stream()
                .map(SubscriptionRecurrenceRule::getDayOfWeek)
                .filter(Objects::nonNull)
                .toList();
        Integer dayOfMonth = rules.get(0).getDayOfMonth();

        return SubscriptionInfo.of(subscription, ruleType, dayOfWeek, dayOfMonth);
    }

    private Map<UUID, List<SubscriptionRecurrenceRule>> findRulesBySubscriptionId(List<Subscription> subscriptions) {
        if (subscriptions.isEmpty()) {
            return Map.of();
        }

        List<UUID> subscriptionIds = subscriptions.stream()
                .map(Subscription::getId)
                .toList();

        List<SubscriptionRecurrenceRule> rules =
                subscriptionRecurrenceRuleRepository
                        .findAllBySubscriptionIdIn(subscriptionIds);

        return rules.stream().collect(Collectors.groupingBy(SubscriptionRecurrenceRule::getSubscriptionId));
    }

    private void updateSubscriptionRecurrenceRule(Subscription subscription, RecurrenceType recurrenceType, List<DayOfWeek> dayOfWeek, Integer dayOfMonth) {
        UUID SubscriptionId = subscription.getId();

        List<SubscriptionRecurrenceRule> newRules = createSubscriptionRecurrenceRule(
                SubscriptionId,
                recurrenceType,
                dayOfWeek,
                dayOfMonth
        );

        subscriptionRecurrenceRuleRepository.deleteAllBySubscriptionId(SubscriptionId);
        subscription.calculateNextRunDate(newRules, LocalDate.now());
        subscriptionRecurrenceRuleRepository.saveAll(newRules);
    }
}
