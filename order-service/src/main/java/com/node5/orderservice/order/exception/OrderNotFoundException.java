package com.node5.orderservice.order.exception;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(UUID orderId) {
        super("해당하는 주문 내역이 없습니다. (orderId: " + orderId + ")");
    }
}
