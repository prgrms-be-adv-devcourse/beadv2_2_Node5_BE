package com.node5.orderservice.order.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Long getNextSequenceNum();

    Optional<Order> findById(UUID orderId);

    Optional<Order> findBySubscriptionKey(UUID subscriptionKey);

    Page<Order> findByMemberIdAndCreatedAtAfterOrderByCreatedAtDesc(UUID memberId, LocalDateTime createdAt, Pageable pageable);

    List<Order> saveAll(List<Order> paidOrders);

    List<Order> findByStatus(OrderStatus orderStatus);

    List<UUID> findRecentOrderIds(UUID memberId, LocalDateTime threeMonthsAgo);
}
