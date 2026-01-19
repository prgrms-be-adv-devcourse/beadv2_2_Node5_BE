package com.node5.paymentservice.payment.application;

import com.node5.paymentservice.payment.application.dto.*;
import com.node5.paymentservice.payment.client.tossPayments.TossPaymentClient;
import com.node5.paymentservice.payment.client.openfeign.WalletClient;
import com.node5.paymentservice.payment.client.openfeign.dto.WalletRequest;
import com.node5.paymentservice.payment.domain.*;
import com.node5.paymentservice.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.node5.paymentservice.payment.exception.PaymentErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;

    private final WalletClient walletClient;
    private final TossPaymentClient tossPaymentClient;

    // memberId로 결제 내역 조회
    public Page<PaymentInfo> findAll(UUID memberId, Pageable pageable) {
        Page<Payment> paymentPage = paymentRepository.findAllByMemberId(memberId, pageable);
        return paymentPage.map(PaymentInfo::from);
    }

    // 결제 실패 기록
    @Transactional
    public PaymentFailureInfo failure(UUID memberId, PaymentFailureCommand command) {
        return paymentRepository.findByOrderId(command.orderId())
                .map(existingPayment -> {
                    // [CASE A] 이미 존재함 -> 상태만 반환 (멱등성 보장)
                    log.info("이미 결제 기록이 존재합니다. 현재 상태를 반환합니다: {}", command.orderId());
                    return PaymentFailureInfo.from(existingPayment.getStatus());
                })
                .orElseGet(() -> {
                    // [CASE B] 존재하지 않음 -> 새로 실패 기록 생성
                    Payment payment = Payment.builder()
                            .memberId(memberId)
                            .orderId(command.orderId())
                            .amount(command.amount())
                            .build();

                    payment.failure(command.errorMessage());
                    paymentRepository.save(payment);

                    log.info("새로운 결제 실패 기록을 생성했습니다: {}", command.orderId());
                    return PaymentFailureInfo.from(payment.getStatus());
                });
    }

    // 결제 취소
    @Transactional
    public PaymentCancelInfo cancel(UUID memberId, PaymentCancelCommand command) {
        Payment payment = paymentRepository.findByOrderId(command.orderId())
                .orElseThrow(() -> new PaymentException(PAYMENT_NOT_FOUND));

        // 결제 키와 금액 검증
        payment.validateValue(memberId, command);

        // 결제 상태 검증
        payment.validateStatus(payment, PaymentStatus.CONFIRMED);

        // 결제 상태 변경
        payment.pending_cancel();
        paymentRepository.saveAndFlush(payment);

        // 예치금 출금 요청
        try {
            WalletRequest withdrawRequest = new WalletRequest(
                    payment.getMemberId(),
                    payment.getOrderId(),
                    payment.getAmount()
            );
            walletClient.withdrawRequest(withdrawRequest);
        } catch (Exception e) {
            log.error("[예치금 출금 요청 실패] 결제 취소 실패 처리 - MemberId: {}, OrderId: {}, Error: {}", payment.getMemberId(), payment.getOrderId(), e.getMessage());
            // 결제 취소 실패 처리
            payment.cancel_failure("예치금 출금 요청 실패: " + e.getMessage());
            throw new PaymentException(PAYMENT_WALLET_WITHDRAW_FAILED); //예치금 출금 요청 실패
        }

        try {
            tossPaymentClient.cancel(command);
            payment.cancel();
        } catch (Exception e) {
            payment.cancel_failure("PG사 결제 취소 실패: " + e.getMessage());
        }

        return PaymentCancelInfo.from(payment.getStatus());
    }
}
