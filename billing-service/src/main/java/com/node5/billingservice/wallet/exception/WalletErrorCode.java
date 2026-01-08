package com.node5.billingservice.wallet.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
public enum WalletErrorCode implements BaseErrorCode {
    WALLET_NOT_FOUND(NOT_FOUND.value(), "WALLET_001", "예치금이 존재하지 않습니다."),
    WALLET_ALREADY_EXISTS(BAD_REQUEST.value(), "WALLET_002", "예치금이 이미 존재합니다."),
    WALLET_WITHDRAW_LOG_NOT_FOUND(NOT_FOUND.value(), "WALLET_003", "예치금 출금 내역이 존재하지 않습니다."),
    WALLET_REFUND_STATE_INVALID(BAD_REQUEST.value(), "WALLET_004", "환불 가능한 상태가 아닙니다."),
    WALLET_ORDER_ID_MISMATCH(BAD_REQUEST.value(), "WALLET_005", "주문 ID가 일치하지 않습니다."),
    WALLET_REFUND_AMOUNT_INVALID(BAD_REQUEST.value(), "WALLET_006", "환불 금액이 일치하지 않습니다."),
    INSUFFICIENT_WALLET_BALANCE(BAD_REQUEST.value(), "WALLET_007", "예치금 잔액이 부족합니다."),
    WALLET_SETTLEMENT_ALREADY_EXISTS(BAD_REQUEST.value(), "WALLET_008", "이미 정산된 요청입니다."),
    WALLET_TRANSFER_BANK_TIMEOUT(BAD_REQUEST.value(), "WALLET_010", "은행 응답 시간이 초과되었습니다."),
    WALLET_TRANSFER_BANK_MAINTENANCE(BAD_REQUEST.value(), "WALLET_011", "은행 점검 시간입니다."),
    WALLET_TRANSFER_INVALID_ACCOUNT(BAD_REQUEST.value(), "WALLET_012", "유효하지 않은 계좌 정보입니다."),
    WALLET_TRANSFER_SYSTEM_ERROR(BAD_REQUEST.value(), "WALLET_013", "내부 시스템 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;

    WalletErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
