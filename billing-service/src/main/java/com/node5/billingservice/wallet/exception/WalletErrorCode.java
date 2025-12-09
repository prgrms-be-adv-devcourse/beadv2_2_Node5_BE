package com.node5.billingservice.wallet.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public enum WalletErrorCode implements BaseErrorCode {
    WALLET_NOT_FOUND(404, "WALLET_001", "예치금이 존재하지 않습니다.");

    private final int status;
    private final String code;
    private final String message;

    WalletErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
