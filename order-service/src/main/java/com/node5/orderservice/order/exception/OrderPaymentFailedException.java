package com.node5.orderservice.order.exception;

import java.util.UUID;

public class OrderPaymentFailedException extends RuntimeException {
    public OrderPaymentFailedException(UUID orderId, String message) {
        super(String.format("결제가 실패했습니다. (orderId: %s, message: %s)", orderId, message));
    }
}
