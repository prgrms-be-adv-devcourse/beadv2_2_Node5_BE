package com.node5.orderservice.order.application;

import com.node5.orderservice.order.domain.OrderItemRepository;
import com.node5.orderservice.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderInternalService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    // 최근 3개월간 Order 조회 후 최대 5개의 OrderItem 반환
    public List<UUID> getRecentOrderList(UUID memberId) {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<UUID> orderIds = orderRepository.findRecentOrderIds(memberId, threeMonthsAgo);
        return orderItemRepository.findTop5IdByOrderIdInOrderByCreatedAtDesc(orderIds);
    }
}
