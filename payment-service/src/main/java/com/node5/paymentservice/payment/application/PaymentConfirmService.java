package com.node5.paymentservice.payment.application;

import com.node5.common.event.PaymentDepositEvent;
import com.node5.common.event.PaymentSendEmailEvent;
import com.node5.paymentservice.payment.client.tossPayments.dto.TossPaymentResponse;
import com.node5.paymentservice.payment.domain.Payment;
import com.node5.paymentservice.payment.domain.PaymentRepository;
import com.node5.paymentservice.payment.exception.PaymentException;
import com.node5.paymentservice.payment.infrastructure.kafka.handler.PaymentEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.node5.paymentservice.payment.exception.PaymentErrorCode.PAYMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConfirmService {
    private final PaymentRepository paymentRepository;
    private final PaymentEventHandler paymentEventHandler;

    @Transactional
    public Payment confirmPaymentProcessing(UUID paymentID, TossPaymentResponse tossPayment) {
        Payment payment = paymentRepository.findById(paymentID);
        if (payment == null) {
            throw new PaymentException(PAYMENT_NOT_FOUND);
        }

        // 결제 상태 수정
        payment.confirm(tossPayment);
        paymentRepository.save(payment);

        // 결제 성공 이메일 이벤트 저장
        PaymentSendEmailEvent emailEvent = new PaymentSendEmailEvent(payment.getMemberId(), payment.getOrderId(), payment.getAmount(), payment.getStatus().toString(), null);
        paymentEventHandler.saveOutbox("PaymentSendEmail", payment.getId(), "payment-service.send-email-event.v1", emailEvent);
        // 입금 이벤트 저장
        PaymentDepositEvent event = new PaymentDepositEvent(payment.getId(), payment.getMemberId(), payment.getOrderId(), payment.getAmount(), LocalDateTime.now());
        paymentEventHandler.saveOutbox("PaymentDeposit", payment.getId(), "payment-service.deposit-event.v1", event);

        return  payment;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsFailed(String orderId, String errorMessage) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentException(PAYMENT_NOT_FOUND));

        payment.failure(errorMessage);
        paymentRepository.save(payment);

        // 결제 실패 이메일 이벤트 저장
        PaymentSendEmailEvent emailEvent = new PaymentSendEmailEvent(payment.getMemberId(), payment.getOrderId(), payment.getAmount(), payment.getStatus().toString(), payment.getFailReason());
        paymentEventHandler.saveOutbox("PaymentSendEmail", payment.getId(), "payment-service.send-email-event.v1", emailEvent);
    }
}
