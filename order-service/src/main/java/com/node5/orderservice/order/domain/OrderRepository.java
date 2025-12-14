package com.node5.orderservice.order.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Long getNextSequenceNum();

    Optional<Order> findById(UUID orderId);

    Page<Order> findByMemberIdAndCreatedAtAfterOrderByCreatedAtDesc(UUID memberId, LocalDateTime createdAt, Pageable pageable);
}
