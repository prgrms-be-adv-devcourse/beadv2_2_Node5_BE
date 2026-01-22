package com.node5.paymentservice.payment.application;

import com.node5.paymentservice.payment.application.dto.PaymentCancelCommand;
import com.node5.paymentservice.payment.domain.Payment;
import com.node5.paymentservice.payment.domain.PaymentRepository;
import com.node5.paymentservice.payment.domain.PaymentStatus;
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
public class PaymentCancelPendingService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment cancelPendingPaymentProcessing(UUID memberId, PaymentCancelCommand command) {
        Payment payment = paymentRepository.findByOrderId(command.orderId())
                .orElseThrow(() -> new PaymentException(PAYMENT_NOT_FOUND));

        // 결제 키와 금액 검증
        payment.validateValue(memberId, command);

        // 결제 상태 검증
        payment.validateStatus(payment, PaymentStatus.CONFIRMED);

        // 결제 상태 변경
        payment.pending_cancel();
        paymentRepository.saveAndFlush(payment);
        return payment;
    }
}
