package com.node5.paymentservice.payment.application;

import com.node5.paymentservice.payment.domain.Payment;
import com.node5.paymentservice.payment.domain.PaymentRepository;
import com.node5.paymentservice.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.node5.paymentservice.payment.exception.PaymentErrorCode.PAYMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentPendingConfirmService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public void pendingConfirmPaymentProcessing(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId);
        if (payment == null) {
            throw new PaymentException(PAYMENT_NOT_FOUND);
        }

        // 결제 상태 수정
        payment.pending_confirm();
    }
}
