package com.node5.paymentservice.payment.exception.cancel;

import com.node5.paymentservice.payment.domain.PaymentFailureOrigin;

public class WithdrawUncertainException extends CancelManualRequiredException {
    public WithdrawUncertainException(PaymentFailureOrigin origin, Throwable cause) {
        super(origin, cause);
    }
}
