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
import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {

    @Query(value = "SELECT nextval('order_num_seq')", nativeQuery = true)
    Long getNextSequenceNum();

    Page<Order> findByMemberIdAndCreatedAtAfterOrderByCreatedAtDesc(UUID memberId, LocalDateTime createdAt, Pageable pageable);

    Optional<Order> findBySubscriptionKey(UUID subscriptionKey);

    List<Order> findByStatus(OrderStatus orderStatus);

    @Query("SELECT o.id FROM Order o " +
            "WHERE o.memberId = :memberId " +
            "AND o.paidAt >= :threeMonthsAgo " +
            "ORDER BY o.paidAt DESC")
    List<UUID> findRecentOrderIds(@Param("memberId") UUID memberId, @Param("threeMonthsAgo") LocalDateTime threeMonthsAgo);
}
