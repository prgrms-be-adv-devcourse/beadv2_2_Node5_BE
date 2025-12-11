package com.node5.subscriptionservice.subscription.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public enum SubscriptionErrorCode implements BaseErrorCode {
    SUBSCRIPTION_NOT_FOUND(404,"SUBSCRIPTION_001", "구독을 찾을 수 없습니다."),
    SUBSCRIPTION_RULE_NOT_FOUND(404,"SUBSCRIPTION_002", "구독의 주기 정보를 찾을 수 없습니다."),
    SUBSCRIPTION_INVALID_STATE(409, "SUBSCRIPTION_003", "현재 구독 상태에서는 해당 작업을 수행할 수 없습니다."),
    INVALID_RECURRENCE_TYPE(500, "SUBSCRIPTION_004", "유효하지 않은 반복 타입입니다."),
    SUBSCRIPTION_ORDER_REQUEST_FAILED(502, "SUBSCRIPTION_005", "구독 제품의 주문 서비스 요청에 실패했습니다."),
    SUBSCRIPTION_PRODUCT_NOT_FOUND(404, "SUBSCRIPTION_006", "상품을 찾을 수 없습니다."),
    SUBSCRIPTION_PRODUCT_REQUEST_FAILED(502, "SUBSCRIPTION_007", "상품 정보 조회에 실패했습니다.");




    private final int status;
    private final String code;
    private final String message;

    SubscriptionErrorCode(int status, String code, String message)
    {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
