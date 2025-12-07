package com.node5.orderservice.application;

import com.node5.orderservice.domain.OrderItem;
import com.node5.orderservice.domain.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    List<OrderItem> saveAll(List<OrderItem> orderItems){
        return orderItemRepository.saveAll(orderItems);
    }

}
