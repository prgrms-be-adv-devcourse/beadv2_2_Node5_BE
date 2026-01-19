package com.node5.paymentservice.payment.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum PaymentErrorCode implements BaseErrorCode {

    PAYMENT_NOT_FOUND(NOT_FOUND.value(), "PAYMENT_001", "결제 정보를 찾을 수 없습니다."),
    TOSS_SECRET_KEY_NOT_FOUND(INTERNAL_SERVER_ERROR.value(), "PAYMENT_002", "토스 시크릿 키가 설정되어 있지 않습니다."),
    PAYMENT_STATUS_INVALID(BAD_REQUEST.value(), "PAYMENT_003", "결제 상태가 유효하지 않습니다."),
    PAYMENT_REDIS_PROCESS_ERROR(BAD_REQUEST.value(), "PAYMENT_004", "결제 요청 처리 중 오류가 발생했습니다."),
    PAYMENT_VALIDATION_FAILED(BAD_REQUEST.value(), "PAYMENT_005", "결제 검증에 실패했습니다."),
    PAYMENT_PG_CONFIRMATION_FAILED(BAD_REQUEST.value(), "PAYMENT_006", "PG사 결제 승인에 실패했습니다."),
    PAYMENT_WALLET_DEPOSIT_FAILED(BAD_REQUEST.value(), "PAYMENT_007", "예치금 입금 요청에 실패했습니다."),
    PAYMENT_WALLET_WITHDRAW_FAILED(BAD_REQUEST.value(), "PAYMENT_008", "예치금 출금 요청에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;

    PaymentErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
