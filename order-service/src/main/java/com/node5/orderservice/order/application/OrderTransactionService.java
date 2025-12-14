package com.node5.orderservice.order.application;

import com.node5.orderservice.order.domain.Order;
import com.node5.orderservice.order.domain.OrderRepository;
import com.node5.orderservice.order.domain.OrderStatus;
import com.node5.orderservice.order.exception.OrderNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderTransactionService {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus status){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.updateStatus(status);
    }
}
