package com.node5.orderservice.infrastructure;

import com.node5.orderservice.domain.Order;
import com.node5.orderservice.domain.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryAdapter implements OrderRepository {

    @Autowired
    private OrderJpaRepository orderJpaRepository;


    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Long getNextSequenceNum() {
        return orderJpaRepository.getNextSequenceNum();
    }

}
