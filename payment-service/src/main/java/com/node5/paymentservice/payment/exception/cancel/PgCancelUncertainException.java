package com.node5.paymentservice.payment.exception.cancel;

import com.node5.paymentservice.payment.domain.PaymentFailureOrigin;

public class PgCancelUncertainException extends CancelManualRequiredException {
    public PgCancelUncertainException(PaymentFailureOrigin origin, Throwable cause) {
        super(origin, cause);
    }
}
