package com.node5.paymentservice.payment.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.paymentservice.payment.application.dto.PaymentCancelCommand;
import com.node5.paymentservice.payment.application.dto.PaymentCancelInfo;
import com.node5.paymentservice.payment.application.dto.PaymentConfirmCommand;
import com.node5.paymentservice.payment.application.dto.PaymentConfirmInfo;
import com.node5.paymentservice.payment.client.tossPayments.TossPaymentClient;
import com.node5.paymentservice.payment.client.tossPayments.dto.TossPaymentResponse;
import com.node5.paymentservice.payment.domain.Payment;
import com.node5.paymentservice.payment.domain.PaymentFailureOrigin;
import com.node5.paymentservice.payment.domain.PaymentTemporaryData;
import com.node5.paymentservice.payment.exception.PaymentException;
import com.node5.paymentservice.payment.exception.cancel.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.util.UUID;

import static com.node5.paymentservice.payment.exception.PaymentErrorCode.*;
import static com.node5.paymentservice.payment.exception.PaymentErrorCode.PAYMENT_VALIDATION_FAILED;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentFacade {
    private final PaymentPendingService paymentPendingService;
    private final PaymentConfirmService paymentConfirmService;
    private final PaymentCancelService paymentCancelService;

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final TossPaymentClient tossPaymentClient;

    public PaymentConfirmInfo confirm(UUID memberId, PaymentConfirmCommand command) {
        // Redis에서 결제 임시 데이터 조회
        PaymentTemporaryData paymentTemporaryData = loadToRedis(memberId, command.orderId(), command.amount());

        // 결제 정보 저장
        UUID paymentId = paymentPendingService.pendingPaymentProcessing(paymentTemporaryData);

        // 토스페이먼츠 결제 승인 요청
        TossPaymentResponse tossPayment = confirmTossPayments(command, paymentTemporaryData);

        // 결제 상태 수정(confirmed) 및 예치금 입금 이벤트 발행
        Payment payment = paymentConfirmService.confirmPaymentProcessing(paymentId, tossPayment);

        return PaymentConfirmInfo.from(payment.getStatus());
    }

    public PaymentCancelInfo cancel(UUID memberId, PaymentCancelCommand command) {
        // 상태 변경 (CONFIRMED -> CANCEL_PENDING)
        Payment payment = paymentCancelService.cancelPendingPaymentProcessing(memberId, command);

        try {
            // 예치금 출금 동기 호출 및 상태 변경 (CANCEL_PENDING -> WITHDRAW_CONFIRMED)
            paymentCancelService.cancelWithdrawPaymentProcessing(payment.getId());
            // 토스페이먼츠 결제 취소 요청
            cancelTossPayments(command);
            // 상태 변경 (CANCEL_PENDING -> CANCELED)
            Payment canceledPayment = paymentCancelService.cancelPaymentProcessing(payment.getId());
            return PaymentCancelInfo.from(canceledPayment.getStatus());
        } catch (WithdrawRejectedException e) {
            paymentCancelService.rollbackCancelPaymentProcessing(payment.getId(), e.getMessage());
            throw new PaymentException(PAYMENT_CANCEL_FAILED);
        } catch (CancelManualRequiredException e) {
            paymentCancelService.markAsManualProcessing(payment.getId(), e.getMessage());
            switch (e.origin()) {
                case PG -> throw new PaymentException(PAYMENT_CANCEL_MANUAL_PROCESSING_REQUIRED_BY_PG);
                case WALLET_SERVICE -> throw new PaymentException(PAYMENT_CANCEL_MANUAL_PROCESSING_REQUIRED_BY_WALLET);
                case PAYMENT_SERVICE -> throw new PaymentException(PAYMENT_CANCEL_MANUAL_PROCESSING_REQUIRED_BY_PAYMENT);
                default -> throw new PaymentException(PAYMENT_CANCEL_MANUAL_PROCESSING_REQUIRED);
            }
        }
    }

    // Redis에서 결제 임시 데이터 조회
    private PaymentTemporaryData loadToRedis(UUID memberId, String orderId, Long amount) {
        String redisKey = "payment:ready:" + orderId;
        String jsonValue = stringRedisTemplate.opsForValue().get(redisKey);
        if (jsonValue == null) {
            throw new PaymentException(PAYMENT_NOT_FOUND); //레디스 데이터가 없음
        }
        PaymentTemporaryData readyData;
        try {
            readyData = objectMapper.readValue(jsonValue, PaymentTemporaryData.class);
            // 결제 검증
            validatePayment(memberId, orderId, amount, readyData);
        } catch (JsonProcessingException e) {
            throw new PaymentException(PAYMENT_REDIS_PROCESS_ERROR); //레디스 데이터 파싱 오류
        }
        return readyData;
    }

    // 결제 검증
    private void validatePayment(UUID memberId, String orderId, Long amount, PaymentTemporaryData paymentTemporaryData) {
        // 회원 ID 검증
        if (!paymentTemporaryData.getMemberId().equals(memberId)) {
            log.error("[Payment Error] MemberId Mismatch: expected {}, but got {}", paymentTemporaryData.getMemberId(), memberId);
            throw new PaymentException(PAYMENT_VALIDATION_FAILED); //회원 ID 불일치
        }
        // 주문 ID 검증
        if (!paymentTemporaryData.getOrderId().equals(orderId)) {
            log.error("[Payment Error] OrderId Mismatch: expected {}, but got {}", paymentTemporaryData.getOrderId(), orderId);
            throw new PaymentException(PAYMENT_VALIDATION_FAILED); //주문 ID 불일치
        }
        // 결제 금액 검증
        if (!paymentTemporaryData.getAmount().equals(amount)) {
            log.error("[Payment Error] Amount Mismatch: expected {}, but got {}", paymentTemporaryData.getAmount(), amount);
            throw new PaymentException(PAYMENT_VALIDATION_FAILED); //결제 금액 불일치
        }
    }

    private TossPaymentResponse confirmTossPayments(PaymentConfirmCommand command, PaymentTemporaryData paymentTemporaryData) {
        try {
            return tossPaymentClient.confirm(command);
        } catch (Exception e) {
            log.error("[PG 승인 실패] - MemberId: {}, OrderId: {}, Error: {}", paymentTemporaryData.getMemberId(), paymentTemporaryData.getOrderId(), e.getMessage());
            paymentConfirmService.markAsFailed(paymentTemporaryData.getOrderId(), e.getMessage());
            throw new PaymentException(PAYMENT_PG_CONFIRMATION_FAILED);
        }
    }

    private void cancelTossPayments(PaymentCancelCommand command) {
        try {
            tossPaymentClient.cancel(command);
        } catch (HttpClientErrorException e) {
            // 4xx 오류
            throw new PgCancelRejectedException(PaymentFailureOrigin.PG, e);
        } catch (HttpServerErrorException | ResourceAccessException e) {
            // 5xx, timeout, 네트워크 오류
            throw new PgCancelUncertainException(PaymentFailureOrigin.PG, e);
        } catch (HttpStatusCodeException e) {
            // 기타 HTTP 상태 코드 오류
            throw new PgCancelUncertainException(PaymentFailureOrigin.PG, e);
        }
    }
}
