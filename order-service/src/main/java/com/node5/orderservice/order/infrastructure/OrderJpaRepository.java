package com.node5.orderservice.order.infrastructure;

import com.node5.orderservice.order.domain.Order;
import com.node5.orderservice.order.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {

    @Query(value = "SELECT nextval('order_num_seq')", nativeQuery = true)
    Long getNextSequenceNum();

    Page<Order> findByMemberIdAndCreatedAtAfterOrderByCreatedAtDesc(UUID memberId, LocalDateTime createdAt, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.paidAt < :standard")
    List<Order> findByStatusAndPaidAtBefore(@Param("status") OrderStatus status, @Param("standard") LocalDateTime standard);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.modifiedAt < :standard")
    List<Order> findByStatusAndModifiedAtBefore(@Param("status") OrderStatus status, @Param("standard") LocalDateTime standard);

    List<Order> findByStatus(OrderStatus orderStatus);
}
