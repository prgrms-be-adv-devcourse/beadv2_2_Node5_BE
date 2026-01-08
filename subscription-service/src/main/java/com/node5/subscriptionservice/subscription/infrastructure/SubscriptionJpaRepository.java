package com.node5.subscriptionservice.subscription.infrastructure;

import com.node5.subscriptionservice.subscription.domain.Subscription;
import com.node5.subscriptionservice.subscription.domain.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SubscriptionJpaRepository extends JpaRepository<Subscription, UUID> {
    Page<Subscription> findAllByMemberId(UUID memberId, Pageable pageable);

    List<Subscription> findAllByMemberId(UUID memberId);

    Page<Subscription> findAllByNextRunDateAndSubscriptionStatus(LocalDate nextRunDate, SubscriptionStatus subscriptionStatus, Pageable pageable);

    Page<Subscription> findAllByProductId(UUID productId, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Subscription s
        set s.subscriptionStatus = 'TERMINATED',
            s.deletedAt = coalesce(s.deletedAt, :dateTime)
        where s.shopId = :shopId
    """)
    void bulkTerminateAllByShop(UUID shopId, LocalDateTime dateTime);
}
