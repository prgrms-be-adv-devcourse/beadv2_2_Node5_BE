package com.node5.paymentservice.payment.exception.cancel;

public class WithdrawRejectedException extends CancelFlowException {
    public WithdrawRejectedException(Throwable cause) {
        super(cause);
    }
}
