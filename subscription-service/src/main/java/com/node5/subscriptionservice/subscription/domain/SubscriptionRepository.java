package com.node5.subscriptionservice.subscription.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository {

    Optional<Subscription> findById(UUID id);

    Subscription save(Subscription subscription);

    List<Subscription> findAllById(List<UUID> ids);

    List<Subscription> saveAll(List<Subscription> subscriptions);

    Page<Subscription> findAllByMemberId(UUID memberId, Pageable pageable);

    List<Subscription> findAllByMemberId(UUID memberId);

    Page<Subscription> findAllByNextRunDateAndSubscriptionStatus(LocalDate nextRunDate, SubscriptionStatus subscriptionStatus, Pageable pageable);

    Page<Subscription> findAllByProductId(UUID productId, Pageable pageable);

    void bulkUpdateNextRunDateByIds(List<UUID> ids, LocalDate nextRunDate);

    void bulkUpdateNextRunDates(java.util.Map<UUID, LocalDate> nextRunDates);

    void bulkMarkFailedByIds(List<UUID> ids);

    void bulkTerminateAllByShop(UUID shopId, LocalDateTime dateTime);
}
