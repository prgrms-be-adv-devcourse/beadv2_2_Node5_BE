package com.node5.orderservice.order.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements BaseErrorCode {
    INVALID_VALUE(400, "ORDER_001", "잘못된 입력값입니다."),
    ORDER_NOT_FOUND(404, "ORDER_002", "해당하는 주문 내역이 없습니다."),
    ORDER_ACCESS_DENIED(403, "ORDER_003", "해당 주문에 대한 권한이 없습니다."),
    ORDER_REQUEST_NOT_ALLOWED(400, "ORDER_004", "해당 요청을 진행할 수 없습니다"),
    ORDER_PAYMENT_FAILED(400, "ORDER_005", "결제가 실패했습니다."),
    ORDER_GET_SHOPID_FAILED(400, "ORDER_006", "Shop Id 조회에 실패했습니다."),
    ORDER_ITEM_NOT_FOUND(404, "ORDER_007", "해당하는 주문 상품 내역이 없습니다."),
    ORDER_STOCK_HOLD_FAILED(400, "ORDER_007", "재고 선점에 실패하여 주문이 불가합니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
