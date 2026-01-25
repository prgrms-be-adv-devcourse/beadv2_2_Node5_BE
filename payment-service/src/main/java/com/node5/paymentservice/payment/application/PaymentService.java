package com.node5.paymentservice.payment.application;

import com.node5.paymentservice.payment.application.dto.*;
import com.node5.paymentservice.payment.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;

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
}
