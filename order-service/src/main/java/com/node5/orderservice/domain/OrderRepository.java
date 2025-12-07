package com.node5.orderservice.domain;

public interface OrderRepository {

    Order save(Order order);

    Long getNextSequenceNum();
}
