package com.node5.orderservice.order.infrastructure;

import com.node5.orderservice.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {

    @Query(value = "SELECT nextval('order_num_seq')", nativeQuery = true)
    Long getNextSequenceNum();

    Page<Order> findByMemberIdAndCreatedAtAfterOrderByCreatedAtDesc(UUID memberId, LocalDateTime createdAt, Pageable pageable);

}
