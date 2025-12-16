package com.node5.billingservice.payment.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum PaymentErrorCode implements BaseErrorCode {

    PAYMENT_NOT_FOUND(NOT_FOUND.value(), "PAYMENT_001", "결제 정보를 찾을 수 없습니다."),
    PAYMENT_KEY_MISMATCH(BAD_REQUEST.value(), "PAYMENT_002", "결제 키가 일치하지 않습니다."),
    PAYMENT_AMOUNT_MISMATCH(BAD_REQUEST.value(), "PAYMENT_003", "결제 금액이 일치하지 않습니다."),
    TOSS_SECRET_KEY_NOT_FOUND(INTERNAL_SERVER_ERROR.value(), "PAYMENT_004", "토스 시크릿 키가 설정되어 있지 않습니다."),
    PAYMENT_MEMBER_ID_MISMATCH(BAD_REQUEST.value(), "PAYMENT_005", "결제 회원 ID가 일치하지 않습니다."),
    PAYMENT_STATUS_INVALID(BAD_REQUEST.value(), "PAYMENT_006", "결제 상태가 유효하지 않습니다.");

    private final int status;
    private final String code;
    private final String message;

    PaymentErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
