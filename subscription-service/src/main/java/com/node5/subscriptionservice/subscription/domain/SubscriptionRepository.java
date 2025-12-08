package com.node5.subscriptionservice.subscription.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository {

    Optional<Subscription> findById(UUID id);

    Subscription save(Subscription subscription);

    Page<Subscription> findAllByMemberId(UUID memberId, Pageable pageable);
}
