package com.node5.settlementservice.settlement.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettlementErrorCode implements BaseErrorCode {
    INVALID_VALUE(400, "SETTLEMENT_001", "잘못된 입력값입니다"),
    SETTLEMENT_ALREADY_COMPLETED(409, "SETTLEMENT_002", "이미 완료된 정산 배치 작업입니다.")
    ;

    private final int status;
    private final String code;
    private final String message;
}
