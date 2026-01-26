package com.node5.paymentservice.payment.exception.cancel;

import com.node5.paymentservice.payment.domain.PaymentFailureOrigin;

public class PgCancelRejectedException extends CancelManualRequiredException {
    public PgCancelRejectedException(PaymentFailureOrigin origin, Throwable cause) {
        super(origin, cause);
    }
}
