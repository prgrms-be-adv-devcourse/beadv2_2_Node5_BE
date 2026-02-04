package com.node5.orderservice.order.infrastructure;

import com.node5.orderservice.order.domain.OrderItemSettlementStatus;
import com.node5.orderservice.order.domain.OrderItem;
import com.node5.orderservice.order.domain.OrderItemRepository;
import com.node5.orderservice.order.domain.OrderProgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
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

    @Override
    public Boolean existsInProgressByMemberId(UUID memberId, Collection<OrderProgress> doneStatus) {
        return orderItemJpaRepository.existsInProgressByMemberId(memberId, doneStatus);
    }

    @Override
    public List<OrderItem> findByStatus(OrderProgress status) {
        return orderItemJpaRepository.findByStatus(status);
    }

    @Override
    public List<OrderItem> findByStatusAndSettlementStatus(OrderProgress status, OrderItemSettlementStatus settlementStatus) {
        return orderItemJpaRepository.findByStatusAndSettlementStatus(status, settlementStatus);
    }

    @Override
    public void updateSettlementStatus(List<UUID> orderItemIds, OrderItemSettlementStatus settlementStatus) {
        orderItemJpaRepository.updateSettlementStatus(orderItemIds, settlementStatus);
    }

    @Override
    public Boolean existsByProductIdInAndSettlementStatus(List<UUID> productIds, OrderItemSettlementStatus settlementStatus) {
        return orderItemJpaRepository.existsByProductIdInAndSettlementStatus(productIds, settlementStatus);
    }

    @Override
    public Optional<OrderItem> findByOrderIdAndProductId(UUID orderId, UUID productId) {
        return orderItemJpaRepository.findByOrderIdAndProductId(orderId, productId);
    }
}
