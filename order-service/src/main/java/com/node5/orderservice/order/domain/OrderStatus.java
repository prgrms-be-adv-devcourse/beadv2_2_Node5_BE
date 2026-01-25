package com.node5.orderservice.order.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    CREATED("주문생성"),
    PAID("결제완료"),
    PAYMENT_FAILED("결제실패"),
    CANCELED("주문취소"),
    REFUNDED("환불완료"),
    SETTLEMENT_REQUESTED("정산준비")
    ;

    private final String name;
}
