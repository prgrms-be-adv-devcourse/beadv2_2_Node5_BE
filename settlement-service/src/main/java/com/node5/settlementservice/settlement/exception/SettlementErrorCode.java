package com.node5.settlementservice.settlement.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettlementErrorCode implements BaseErrorCode {
    INVALID_VALUE(400, "SETTLEMENT_001", "잘못된 입력값입니다"),
    ACCESS_DENIED(401, "SETTLEMENT_002", "판매자만 접근 가능 요청입니다.");


    private final int status;
    private final String code;
    private final String message;
}
