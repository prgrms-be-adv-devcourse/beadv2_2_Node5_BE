package com.node5.subscriptionservice.subscription.infrastructure;

import com.node5.subscriptionservice.subscription.domain.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriptionJpaRepository extends JpaRepository<Subscription, UUID> {
    Page<Subscription> findAllByMemberId(UUID memberId, Pageable pageable);
}
