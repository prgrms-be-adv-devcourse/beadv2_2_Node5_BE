package com.node5.orderservice.order.infrastructure;

import com.node5.orderservice.order.domain.Order;
import com.node5.orderservice.order.domain.OrderRepository;
import com.node5.orderservice.order.domain.OrderStatus;
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
    public List<Order> findByStatusAndPaidAtBefore(OrderStatus orderStatus, LocalDateTime standard) {
        return orderJpaRepository.findByStatusAndPaidAtBefore(orderStatus, standard);
    }

    @Override
    public List<Order> findByStatusAndModifiedAtBefore(OrderStatus orderStatus, LocalDateTime standard) {
        return orderJpaRepository.findByStatusAndModifiedAtBefore(orderStatus, standard);
    }

    @Override
    public List<Order> saveAll(List<Order> orders) {
        return orderJpaRepository.saveAll(orders);
    }

    @Override
    public List<Order> findByStatus(OrderStatus orderStatus) {
        return orderJpaRepository.findByStatus(orderStatus);
    }

    @Override
    public List<UUID> findRecentOrderIds(UUID memberId, LocalDateTime threeMonthsAgo) {
        return orderJpaRepository.findRecentOrderIds(memberId, threeMonthsAgo);
    }
}
