package com.node5.orderservice.order.infrastructure;

import com.node5.orderservice.order.domain.Order;
import com.node5.orderservice.order.domain.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public class OrderRepositoryAdapter implements OrderRepository {

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Long getNextSequenceNum() {
        return orderJpaRepository.getNextSequenceNum();
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return orderJpaRepository.findById(orderId);
    }

    @Override
    public Optional<Order> findBySubscriptionKey(UUID subscriptionKey) {
        return orderJpaRepository.findBySubscriptionKey(subscriptionKey);
    }

    @Override
    public Page<Order> findByMemberIdAndCreatedAtAfterOrderByCreatedAtDesc(UUID memberId, LocalDateTime createdAt, Pageable pageable) {
        return orderJpaRepository.findByMemberIdAndCreatedAtAfterOrderByCreatedAtDesc(memberId, createdAt, pageable);
    }

    @Override
    public List<UUID> findRecentOrderIds(UUID memberId, LocalDateTime threeMonthsAgo) {
        return orderJpaRepository.findRecentOrderIds(memberId, threeMonthsAgo);
    }

    @Override
    public boolean existsByIdAndMemberId(UUID orderId, UUID memberId) {
        return orderJpaRepository.existsByIdAndMemberId(orderId, memberId);
    }
}
