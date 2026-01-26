package com.node5.batchservice.settlement.exception;

import com.node5.common.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettlementBatchErrorCode implements BaseErrorCode {
    SETTLEMENT_ALREADY_COMPLETED(409, "SETTLEMENT_BATCH_001", "이미 완료된 정산 배치 작업입니다."),
    BATCH_JOB_LAUNCH_FAILED(500, "SETTLEMENT_BATCH_002", "정산 배치 작업 시작에 실패했습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
