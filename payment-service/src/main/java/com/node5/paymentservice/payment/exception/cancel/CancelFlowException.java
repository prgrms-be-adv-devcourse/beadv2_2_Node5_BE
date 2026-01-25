package com.node5.paymentservice.payment.exception.cancel;

public abstract class CancelFlowException extends RuntimeException {
    public CancelFlowException(Throwable cause) {
        super(cause);
    }
}
