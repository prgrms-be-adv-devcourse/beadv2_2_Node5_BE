package com.node5.orderservice.application.dto;

import com.node5.orderservice.domain.Order;
import com.node5.orderservice.domain.OrderStatus;
import com.node5.orderservice.domain.OrderType;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public record OrderListInfo(
        UUID orderId,
        String orderNum,
        String orderDate,
        OrderStatus status,
        OrderType orderType,
        UUID subscriptionId,
        BigDecimal totalAmount,

        List<OrderItemInfo> orderedItems
) {

    public static OrderListInfo from(Order order, List<OrderItemInfo> orderedItems) {
        // 주문 날짜 포맷팅
        DateTimeFormatter orderDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        String orderDate = order.getCreatedAt().format(orderDateFormatter);

        return new OrderListInfo(
                order.getId(),
                order.getOrderNum(),
                orderDate,
                order.getStatus(),
                order.getOrderType(),
                order.getSubscriptionId(),
                order.getTotalAmount(),
                orderedItems
        );
    }
}
