package com.node5.paymentservice.payment.application;

import com.node5.common.event.PaymentDepositEvent;
import com.node5.paymentservice.payment.client.tossPayments.dto.TossPaymentResponse;
import com.node5.paymentservice.payment.domain.Payment;
import com.node5.paymentservice.payment.domain.PaymentRepository;
import com.node5.paymentservice.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.node5.paymentservice.payment.exception.PaymentErrorCode.PAYMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConfirmService {
    private final PaymentRepository paymentRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Payment confirmPaymentProcessing(UUID paymentID, TossPaymentResponse tossPayment) {
        Payment payment = paymentRepository.findById(paymentID);
        if (payment == null) {
            throw new PaymentException(PAYMENT_NOT_FOUND);
        }

        // 결제 상태 수정
        payment.confirm(tossPayment);

        // 결제 저장
        paymentRepository.save(payment);

        // Wallet으로 이벤트 발행
        PaymentDepositEvent event = new PaymentDepositEvent(payment.getMemberId(), payment.getOrderId(), payment.getAmount());
        eventPublisher.publishEvent(event);

        return  payment;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsFailed(String orderId, String errorMessage) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentException(PAYMENT_NOT_FOUND));

        payment.failure(errorMessage);
        paymentRepository.save(payment);
    }
}
