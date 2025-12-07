package com.node5.orderservice.domain;

import java.util.List;

public interface OrderItemRepository {

    List<OrderItem> saveAll(List<OrderItem> items);
}
