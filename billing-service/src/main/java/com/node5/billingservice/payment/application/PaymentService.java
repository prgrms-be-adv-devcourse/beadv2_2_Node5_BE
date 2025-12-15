package com.node5.billingservice.payment.application;

import com.node5.billingservice.payment.application.dto.*;
import com.node5.billingservice.payment.client.dto.TossPaymentResponse;
import com.node5.billingservice.payment.client.TossPaymentClient;
import com.node5.billingservice.payment.domain.Payment;
import com.node5.billingservice.payment.domain.PaymentFailure;
import com.node5.billingservice.payment.exception.PaymentException;
import com.node5.billingservice.payment.infrastructure.PaymentFailureRepositoryAdapter;
import com.node5.billingservice.payment.infrastructure.PaymentRepositoryAdapter;
import com.node5.billingservice.wallet.domain.Wallet;
import com.node5.billingservice.wallet.exception.WalletException;
import com.node5.billingservice.wallet.infrastructure.WalletRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static com.node5.billingservice.payment.exception.PaymentErrorCode.*;
import static com.node5.billingservice.wallet.exception.WalletErrorCode.WALLET_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private static final Log log = LogFactory.getLog(PaymentService.class);
    private final PaymentRepositoryAdapter paymentRepositoryAdapter;
    private final PaymentFailureRepositoryAdapter paymentFailureRepositoryAdapter;
    private final WalletRepositoryAdapter walletRepositoryAdapter;

    private final TossPaymentClient tossPaymentClient;

    // walletId로 결제 내역 조회
    public Page<PaymentInfo> findAll(UUID memberId, Pageable pageable) {

        walletRepositoryAdapter.findByMemberId(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        Page<Payment> paymentPage = paymentRepositoryAdapter.findAllByMemberId(memberId, pageable);
        return paymentPage.map(PaymentInfo::from);
    }

    // 결제 요청
    @Transactional
    public PaymentInfo request(UUID memberId, PaymentCommand command) {

        walletRepositoryAdapter.findByMemberId(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        Payment payment = Payment.builder()
                .memberId(memberId)
                .amount(command.amount())
                .build();
        paymentRepositoryAdapter.save(payment);
        return PaymentInfo.from(payment);
    }

    //결제 승인
    @Transactional
    public PaymentInfo confirm(UUID memberId, PaymentConfirmCommand command) {

        Wallet wallet = walletRepositoryAdapter.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        Payment payment = paymentRepositoryAdapter.findByOrderId(command.orderId())
                .orElseThrow(() -> new PaymentException(PAYMENT_NOT_FOUND));

        // 결제 금액 검증
        if (!Objects.equals(payment.getAmount(), command.amount())) {
            throw new PaymentException(PAYMENT_AMOUNT_MISMATCH);
        }

        TossPaymentResponse tossPayment = tossPaymentClient.confirm(command);
        payment.confirm(tossPayment);
        paymentRepositoryAdapter.save(payment);
        wallet.deposit(tossPayment.totalAmount());

        log.info("Payment confirmed: " + payment.getId());
        return PaymentInfo.from(payment);
    }

    // 결제 실패 기록
    @Transactional
    public PaymentFailureInfo failure(UUID memberId, PaymentFailureCommand command) {
        walletRepositoryAdapter.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        Payment payment = paymentRepositoryAdapter.findByOrderId(command.orderId())
                .orElseThrow(() -> new PaymentException(PAYMENT_NOT_FOUND));

        // 결제 금액 검증
        if (!Objects.equals(payment.getAmount(), command.amount())) {
            throw new PaymentException(PAYMENT_AMOUNT_MISMATCH);
        }
        payment.failure(command.errorMessage());

        PaymentFailure failure = PaymentFailure.builder()
                .memberId(memberId)
                .orderId(payment.getOrderId())
                .errorCode(command.errorCode())
                .errorMessage(command.errorMessage())
                .amount(payment.getAmount())
                .build();

        paymentFailureRepositoryAdapter.save(failure);
        return PaymentFailureInfo.from(failure);
    }

    // 결제 취소
    @Transactional
    public PaymentInfo cancel(UUID memberId, PaymentCancelCommand command) {
        Wallet wallet = walletRepositoryAdapter.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        Payment payment = paymentRepositoryAdapter.findByOrderId(command.orderId())
                .orElseThrow(() -> new PaymentException(PAYMENT_NOT_FOUND));

        // 결제 키와 금액 검증
        payment.validateValue(memberId, payment, command.paymentKey(), command.amount());

        // 예치금 잔액 검증
        wallet.validateSufficientBalance(command.amount());

        // 결제 취소 요청
        TossPaymentResponse tossPayment = tossPaymentClient.cancel(command);
        payment.cancel();
        paymentRepositoryAdapter.save(payment);
        wallet.withdraw(tossPayment.totalAmount());

        log.info("Payment canceled: " + payment.getId());
        return PaymentInfo.from(payment);
    }
}
