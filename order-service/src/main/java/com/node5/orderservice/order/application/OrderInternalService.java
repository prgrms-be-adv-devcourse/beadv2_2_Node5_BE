package com.node5.orderservice.order.application;

import com.node5.orderservice.order.application.dto.OrderStatusCommand;
import com.node5.orderservice.order.domain.OrderItemRepository;
import com.node5.orderservice.order.domain.OrderItemSettlementStatus;
import com.node5.orderservice.order.domain.OrderProgress;
import com.node5.orderservice.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderInternalService {

    private static final int RECENT_ORDER_ITEM_CNT = 5;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    // 최근 3개월간 Order 조회 후 RECENT_ORDER_ITEM_CNT개의 OrderItem의 productId 반환
    public List<UUID> getRecentOrderList(UUID memberId) {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);

        List<UUID> orderIds = orderRepository.findRecentOrderIds(memberId, threeMonthsAgo);
        if (orderIds == null || orderIds.isEmpty()) {
            return Collections.emptyList();
        }

        return orderItemRepository.findRecentProductIds(orderIds, PageRequest.of(0, RECENT_ORDER_ITEM_CNT));
    }

    // orderId, productId로 OrderItem의 status 조회
    public Boolean getOrderStatus(UUID memberId, OrderStatusCommand command) {
        // Order 확인
        boolean existsMyOrder = orderRepository.existsByIdAndMemberId(command.orderId(), memberId);
        if(!existsMyOrder) return false;

        // OrderItem 확인
        OrderProgress status = orderItemRepository
                .findStatusByOrderIdAndProductId(command.orderId(), command.productId())
                .orElse(null);
        return status == OrderProgress.CONFIRMED;
    }

    // 진행 중인 주문이 있는지 확인
    public Boolean hasInProgressOrder(UUID memberId) {
        return orderItemRepository.existsInProgressByMemberId(
                memberId,
                List.of(OrderProgress.CONFIRMED, OrderProgress.REFUND_COMPLETED)
        );
    }

    // 정산 대기 중인 주문 상품이 있는지 확인
    public Boolean hasInProgressSettlementPending(List<UUID> productIds) {
        return orderItemRepository.existsByProductIdInAndSettlementStatus(productIds, OrderItemSettlementStatus.PENDING);
    }
}
