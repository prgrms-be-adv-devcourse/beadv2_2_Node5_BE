package com.node5.orderservice.infrastructure;

import com.node5.orderservice.domain.OrderItem;
import com.node5.orderservice.domain.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderItemRepositoryAdapter implements OrderItemRepository {

    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;

    public List<OrderItem> saveAll(List<OrderItem> orderItems) {
        return orderItemJpaRepository.saveAll(orderItems);
    }

}
