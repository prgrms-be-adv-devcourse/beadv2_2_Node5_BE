package com.node5.orderservice.subscription.infrastructure;

import com.node5.orderservice.subscription.domain.Subscription;
import com.node5.orderservice.subscription.domain.SubscriptionStatus;
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

    List<Subscription> findAllByShopId(UUID shopId);

    Page<Subscription> findAllByNextRunDateAndSubscriptionStatusIn(LocalDate nextRunDate, List<SubscriptionStatus> subscriptionStatuses, Pageable pageable);

    Page<Subscription> findAllByProductId(UUID productId, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Subscription s
        set s.subscriptionStatus = 'TERMINATED',
            s.deletedAt = coalesce(s.deletedAt, :dateTime)
        where s.shopId = :shopId
            and s.subscriptionStatus != 'TERMINATED'
    """)
    void bulkTerminateAllByShop(UUID shopId, LocalDateTime dateTime);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Subscription s
        set s.nextRunDate = :nextRunDate
        where s.id in :ids
    """)
    void bulkUpdateNextRunDateByIds(List<UUID> ids, LocalDate nextRunDate);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Subscription s
        set s.subscriptionStatus = 'FAILED'
        where s.id in :ids
            and s.subscriptionStatus not in ('CANCELLED', 'UNAVAILABLE', 'TERMINATED')
    """)
    void bulkMarkFailedByIds(List<UUID> ids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Subscription s
        set s.subscriptionStatus = 'UNAVAILABLE'
        where s.id in :ids
            and s.subscriptionStatus not in ('CANCELLED', 'UNAVAILABLE', 'TERMINATED')
    """)
    void bulkMarkUnavailableByIds(List<UUID> ids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Subscription s
        set s.subscriptionStatus = 'ACTIVE'
        where s.id in :ids
            and s.subscriptionStatus = 'FAILED'
    """)
    void bulkMarkActiveByIds(List<UUID> ids);
}
