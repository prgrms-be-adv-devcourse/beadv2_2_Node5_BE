package com.node5.orderservice.order.exception;

import com.node5.common.exception.BaseErrorCode;
import com.node5.common.exception.BaseException;
import lombok.Getter;

@Getter
public class OrderException extends BaseException {

    private final String customMessage;

    public OrderException(BaseErrorCode errorCode) {
        super(errorCode);
        this.customMessage = null;
    }

    public OrderException(BaseErrorCode errorCode, String customMessage) {
        super(errorCode);
        this.customMessage = customMessage;
    }
}
