package com.node5.paymentservice.payment.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.common.event.PaymentDepositEvent;
import com.node5.paymentservice.payment.client.tossPayments.dto.TossPaymentResponse;
import com.node5.paymentservice.payment.domain.Payment;
import com.node5.paymentservice.payment.domain.PaymentOutbox;
import com.node5.paymentservice.payment.domain.PaymentOutboxRepository;
import com.node5.paymentservice.payment.domain.PaymentRepository;
import com.node5.paymentservice.payment.exception.PaymentException;
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
    private final PaymentOutboxRepository paymentOutboxRepository;

    private final ObjectMapper objectMapper;

    @Transactional
    public Payment confirmPaymentProcessing(UUID paymentID, TossPaymentResponse tossPayment) {
        Payment payment = paymentRepository.findById(paymentID);
        if (payment == null) {
            throw new PaymentException(PAYMENT_NOT_FOUND);
        }

        // 결제 상태 수정
        payment.confirm(tossPayment);
        paymentRepository.save(payment);

        PaymentDepositEvent event = new PaymentDepositEvent(payment.getId(), payment.getMemberId(), payment.getOrderId(), payment.getAmount(), LocalDateTime.now());
        try {
            paymentOutboxRepository.save(
                    PaymentOutbox.ready(
                            "PaymentDeposit",
                            payment.getId(),
                            "PAYMENT_DEPOSIT_REQUESTED",
                            objectMapper.writeValueAsString(event)
                    )
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

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
