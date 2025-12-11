package com.node5.billingservice.payment.exception;

import com.node5.common.exception.BaseException;

public class PaymentException extends BaseException {
    public PaymentException(PaymentErrorCode paymentErrorCode) {
        super(paymentErrorCode);
    }
}
