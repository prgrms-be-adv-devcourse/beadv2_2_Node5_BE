package com.node5.orderservice.order.domain;

import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderItemRepository {

    List<OrderItem> saveAll(List<OrderItem> items);

    List<OrderItem> findByOrderId(UUID orderId);

    List<OrderItem> findByOrderIdIn(List<UUID> orderIds);

    List<UUID> findRecentProductIds(List<UUID> orderIds, Pageable pageable);

    void updateStatusByCreatedAtBefore(OrderProgress fromStatus, OrderProgress toStatus);

    Optional<OrderProgress> findStatusByOrderIdAndProductId(UUID orderId, UUID productId);

    Boolean existsInProgressByMemberId(UUID memberId, Collection<OrderProgress> doneStatus);
}
