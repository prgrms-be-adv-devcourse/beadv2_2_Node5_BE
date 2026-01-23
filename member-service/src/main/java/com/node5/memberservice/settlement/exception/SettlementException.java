package com.node5.memberservice.settlement.exception;

import com.node5.common.exception.BaseErrorCode;
import com.node5.common.exception.BaseException;
import lombok.Getter;

@Getter
public class SettlementException extends BaseException {

    private final String customMessage;

    public SettlementException(BaseErrorCode errorCode) {
        super(errorCode);
        this.customMessage = null;
    }

    public SettlementException(BaseErrorCode errorCode, String customMessage) {
        super(errorCode);
        this.customMessage = customMessage;
    }
}
