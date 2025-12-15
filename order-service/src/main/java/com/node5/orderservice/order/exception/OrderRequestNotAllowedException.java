package com.node5.orderservice.order.exception;

import com.node5.orderservice.order.domain.OrderStatus;

import java.util.UUID;

public class OrderRequestNotAllowedException extends RuntimeException{
    public OrderRequestNotAllowedException(UUID orderId, OrderStatus orderStatus, String message) {
        super(String.format("해당 요청을 진행할 수 없습니다: %s (orderId: %s, orderStatus: %s)", message, orderId, orderStatus));
    }
}
