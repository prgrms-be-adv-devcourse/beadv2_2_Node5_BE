package com.node5.orderservice.infrastructure;

import com.node5.orderservice.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findByOrderId(UUID orderId);

    List<OrderItem> findByOrderIdIn(List<UUID> orderIds);

}
