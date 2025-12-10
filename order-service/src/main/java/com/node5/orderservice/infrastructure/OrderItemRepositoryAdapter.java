package com.node5.orderservice.infrastructure;

import com.node5.orderservice.domain.OrderItem;
import com.node5.orderservice.domain.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
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

}
