package com.node5.paymentservice.payment.application;

import com.node5.common.event.PaymentSendEmailEvent;
import com.node5.paymentservice.payment.domain.Payment;
import com.node5.paymentservice.payment.domain.PaymentRepository;
import com.node5.paymentservice.payment.exception.PaymentException;
import com.node5.paymentservice.payment.infrastructure.kafka.handler.PaymentEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.node5.paymentservice.payment.exception.PaymentErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCancelService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventHandler paymentEventHandler;

    @Transactional
    public Payment cancelPaymentProcessing(Payment payment) {
        payment.cancel();
        PaymentSendEmailEvent emailEvent = new PaymentSendEmailEvent(payment.getMemberId(), payment.getOrderId(), payment.getAmount(), payment.getStatus().toString(), null);
        paymentEventHandler.saveOutbox("PaymentSendEmail", payment.getId(), "payment-service.send-email-event.v1", emailEvent);
        return payment;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsFailed(UUID uuid, Exception e) {
        Payment payment = paymentRepository.findById(uuid);
        if (payment == null) {
            throw new PaymentException(PAYMENT_NOT_FOUND);
        }
        payment.cancel_failure("PG사 결제 취소 실패: " + e.getMessage());
        paymentRepository.save(payment);

        // 결제 취소 실패 이메일 이벤트 저장
        PaymentSendEmailEvent emailEvent = new PaymentSendEmailEvent(payment.getMemberId(), payment.getOrderId(), payment.getAmount(), payment.getStatus().toString(), payment.getFailReason());
        paymentEventHandler.saveOutbox("PaymentSendEmail", payment.getId(), "payment-service.send-email-event.v1", emailEvent);
    }
}
