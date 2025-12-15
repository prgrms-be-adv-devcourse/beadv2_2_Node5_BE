package com.node5.orderservice.domain;

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

    Page<Order> findByMemberIdAndCreatedAtAfterOrderByCreatedAtDesc(UUID memberId, LocalDateTime createdAt, Pageable pageable);

    List<Order> findByStatusAndPaidAtBefore(OrderStatus orderStatus, LocalDateTime standard);

    List<Order> findByStatusAndModifiedAtBefore(OrderStatus orderStatus, LocalDateTime standard);

    List<Order> saveAll(List<Order> paidOrders);
}
