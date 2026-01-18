package com.node5.orderservice.order.infrastructure;

import com.node5.orderservice.order.domain.OrderItem;
import com.node5.orderservice.order.domain.OrderItemRepository;
import com.node5.orderservice.order.domain.OrderProgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderItemRepositoryAdapter implements OrderItemRepository {

    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;

    public List<OrderItem> saveAll(List<OrderItem> orderItems) {
        return orderItemJpaRepository.saveAll(orderItems);
    }

    @Override
    public List<OrderItem> findByOrderId(UUID orderId) {
        return orderItemJpaRepository.findByOrderId(orderId);
    }

    @Override
    public List<OrderItem> findByOrderIdIn(List<UUID> orderIds) {
        return orderItemJpaRepository.findByOrderIdIn(orderIds);
    }

    @Override
    public List<UUID> findRecentProductIds(List<UUID> orderIds, Pageable pageable) {
        return orderItemJpaRepository.findRecentProductIds(orderIds, pageable);
    }

    @Override
    public void updateStatusByCreatedAtBefore(OrderProgress fromStatus, OrderProgress toStatus) {
        orderItemJpaRepository.updateStatusByCreatedAtBefore(fromStatus, toStatus);
    }

    @Override
    public Optional<OrderProgress> findStatusByOrderIdAndProductId(UUID orderId, UUID productId) {
        return orderItemJpaRepository.findStatusByOrderIdAndProductId(orderId, productId);
    }

}
