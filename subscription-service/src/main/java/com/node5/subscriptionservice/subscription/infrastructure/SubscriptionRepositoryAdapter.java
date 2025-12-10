package com.node5.subscriptionservice.subscription.infrastructure;

import com.node5.subscriptionservice.subscription.domain.Subscription;
import com.node5.subscriptionservice.subscription.domain.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryAdapter implements SubscriptionRepository {

    private final SubscriptionJpaRepository jpaRepository;

    @Override
    public Optional<Subscription> findById(UUID id){return jpaRepository.findById(id);}

    @Override
    public Subscription save(Subscription subscription){return jpaRepository.save(subscription);}

    @Override
    public Page<Subscription> findAllByMemberId(UUID memberId, Pageable pageable){return jpaRepository.findAllByMemberId(memberId,pageable);}
}
