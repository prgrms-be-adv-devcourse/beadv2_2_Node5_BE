package com.node5.orderservice.order.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderProgress {
    PAID("결제완료"),
    DELIVERY_ING("배송중"),
    DELIVERY_COMPLETED("배송완료"),
    REFUND_PENDING("환불처리중"),
    REFUND_COMPLETED("환불완료"),
    CONFIRMED("구매확정"),
    ;

    private final String name;
}
