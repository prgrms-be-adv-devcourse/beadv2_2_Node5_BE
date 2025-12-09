package com.node5.billingservice.wallet.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public enum WalletErrorCode implements BaseErrorCode {
    WALLET_NOT_FOUND(404, "WALLET_001", "예치금이 존재하지 않습니다."),
    WALLET_LOG_NOT_FOUND(404, "WALLET_002", "예치금 내역이 존재하지 않습니다."),
    PAYMENT_KEY_NOT_FOUND(404, "WALLET_003", "결제키가 존재하지 않습니다."),
    PAYMENT_KEY_MISMATCH(400, "WALLET_004", "결제키가 일치하지 않습니다."),
    REFUND_STATE_INVALID(400, "WALLET_005", "환불 상태가 유효하지 않습니다."),
    INSUFFICIENT_WALLET_BALANCE(400, "WALLET_006", "예치금 잔액이 부족합니다.");

    private final int status;
    private final String code;
    private final String message;

    WalletErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
