package com.node5.paymentservice.payment.exception.cancel;

import com.node5.paymentservice.payment.domain.PaymentFailureOrigin;

public abstract class CancelManualRequiredException extends RuntimeException {
    private final PaymentFailureOrigin origin;

    public CancelManualRequiredException(PaymentFailureOrigin origin, Throwable cause) {
        super(cause);
        this.origin = origin;
    }

    public PaymentFailureOrigin origin() {
        return origin;
    }
}
