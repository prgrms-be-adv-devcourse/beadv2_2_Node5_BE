package com.node5.orderservice.order.application;

import com.node5.orderservice.order.domain.OrderItemRepository;
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
}
