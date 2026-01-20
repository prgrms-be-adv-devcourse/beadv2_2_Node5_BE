package com.node5.paymentservice.payment.application;

import com.node5.paymentservice.payment.client.openfeign.WalletClient;
import com.node5.paymentservice.payment.client.openfeign.dto.WalletRequest;
import com.node5.paymentservice.payment.domain.Payment;
import com.node5.paymentservice.payment.domain.PaymentRepository;
import com.node5.paymentservice.payment.domain.PaymentTemporaryData;
import com.node5.paymentservice.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.node5.paymentservice.payment.exception.PaymentErrorCode.PAYMENT_WALLET_DEPOSIT_FAILED;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentPendingService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public UUID pendingPaymentProcessing(PaymentTemporaryData paymentTemporaryData) {
        log.info("Pending payment processing started.");
        Payment payment = Payment.builder()
                .memberId(paymentTemporaryData.getMemberId())
                .orderId(paymentTemporaryData.getOrderId())
                .amount(paymentTemporaryData.getAmount())
                .build();
        paymentRepository.save(payment);
        log.info("Payment record created with ID: {}", payment.getId());
        return payment.getId();
    }
}
