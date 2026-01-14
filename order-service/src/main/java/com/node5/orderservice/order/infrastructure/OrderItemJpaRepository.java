package com.node5.orderservice.order.infrastructure;

import com.node5.orderservice.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findByOrderId(UUID orderId);

    List<OrderItem> findByOrderIdIn(List<UUID> orderIds);

    @Query("SELECT oi.id " +
            "FROM OrderItem oi " +
            "WHERE oi.orderId IN :orderIds " +
            "ORDER BY oi.createdAt DESC")
    List<UUID> findTop5IdByOrderIdInOrderByCreatedAtDesc(@Param("orderIds") List<UUID> orderIds);
}
