package com.node5.orderservice.order.application.dto;

import com.node5.orderservice.order.domain.Order;
import com.node5.orderservice.order.domain.OrderItem;

import java.util.List;

public record OrderWithItems (
        Order order,
        List<OrderItem> items
) {
}
