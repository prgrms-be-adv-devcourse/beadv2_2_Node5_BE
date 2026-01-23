package com.node5.memberservice.settlement.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettlementErrorCode implements BaseErrorCode {

    INVALID_VALUE(400, "SETTLEMENT_001", "잘못된 입력값입니다"),
    SETTLEMENT_FEIGN_ERROR(400, "SETTLEMENT_002", "Feign 연동 문제가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;
}
