package com.node5.batchservice.settlement.exception;

import com.node5.common.exception.BaseErrorCode;
import com.node5.common.exception.BaseException;
import lombok.Getter;

@Getter
public class SettlementBatchException extends BaseException {
    private final String customMessage;

    public SettlementBatchException(BaseErrorCode errorCode) {
        super(errorCode);
        this.customMessage = null;
    }

    public SettlementBatchException(BaseErrorCode errorCode, String customMessage) {
        super(errorCode);
        this.customMessage = customMessage;
    }
}
