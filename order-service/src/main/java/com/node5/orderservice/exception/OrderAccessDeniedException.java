package com.node5.orderservice.exception;

import java.util.UUID;

public class OrderAccessDeniedException extends RuntimeException {
    public OrderAccessDeniedException(UUID orderId, UUID memberId, String label) {
        super(String.format("해당 주문에 대해 %s 권한이 없습니다. (memberId: %s, orderId: %s)", label, memberId, orderId));
    }
}
