package com.node5.orderservice.order.exception;


public class OrderGetShopIdFailed extends RuntimeException{
    public OrderGetShopIdFailed() {
        super("Shop Id 조회에 실패했습니다.");
    }
}
