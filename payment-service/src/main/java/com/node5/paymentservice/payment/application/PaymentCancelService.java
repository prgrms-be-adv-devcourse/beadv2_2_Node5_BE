package com.node5.paymentservice.payment.application;

import com.node5.common.event.PaymentSendEmailEvent;
import com.node5.paymentservice.payment.application.dto.PaymentCancelCommand;
import com.node5.paymentservice.payment.client.openfeign.WalletClient;
import com.node5.paymentservice.payment.client.openfeign.dto.WalletRequest;
import com.node5.paymentservice.payment.domain.Payment;
import com.node5.paymentservice.payment.domain.PaymentFailureOrigin;
import com.node5.paymentservice.payment.domain.PaymentRepository;
import com.node5.paymentservice.payment.domain.PaymentStatus;
import com.node5.paymentservice.payment.exception.PaymentException;
import com.node5.paymentservice.payment.exception.cancel.WithdrawRejectedException;
import com.node5.paymentservice.payment.exception.cancel.WithdrawUncertainException;
import com.node5.paymentservice.payment.infrastructure.kafka.handler.PaymentEventHandler;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.node5.paymentservice.payment.exception.PaymentErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCancelService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventHandler paymentEventHandler;
    private final WalletClient walletClient;

    // 결제 취소 요청 처리
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
        return payment;
    }

    // 출금 요청 처리
    @Transactional
    public void cancelWithdrawPaymentProcessing(UUID paymentId) {
        Payment payment = findPayment(paymentId);
        try {
            WalletRequest withdrawRequest = new WalletRequest(
                    payment.getMemberId(),
                    payment.getOrderId(),
                    payment.getAmount()
            );
            walletClient.withdrawRequest(withdrawRequest);
            payment.withdraw_confirmed();
        } catch (FeignException e) {
            if (e.status() >= 400 && e.status() < 500) {
                throw new WithdrawRejectedException(e);
            }
            throw new WithdrawUncertainException(PaymentFailureOrigin.WALLET_SERVICE, e);
        }
    }

    // 결제 취소 처리
    @Transactional
    public Payment cancelPaymentProcessing(UUID paymentId) {
        Payment payment = findPayment(paymentId);
        payment.cancel();
        log.info("[결제 취소 완료] - PaymentId: {}, OrderId: {}", payment.getId(), payment.getOrderId());
        PaymentSendEmailEvent emailEvent = new PaymentSendEmailEvent(payment.getMemberId(), payment.getOrderId(), payment.getAmount(), payment.getStatus().toString(), null);
        paymentEventHandler.saveOutbox("PaymentSendEmail", payment.getId(), "payment-service.send-email-event.v1", emailEvent);
        return payment;
    }

    // 결제 취소 롤백 처리
    @Transactional
    public void rollbackCancelPaymentProcessing(UUID paymentId, String message) {
        Payment payment = findPayment(paymentId);
        payment.rollbackConfirmation("결제 취소 실패: " + message);
        log.error("[결제 취소 롤백] - PaymentId: {}, OrderId: {}, Reason: {}", payment.getId(), payment.getOrderId(), message);
        PaymentSendEmailEvent emailEvent = new PaymentSendEmailEvent(payment.getMemberId(), payment.getOrderId(), payment.getAmount(), payment.getStatus().toString(), payment.getFailReason());
        paymentEventHandler.saveOutbox("PaymentSendEmail", payment.getId(), "payment-service.send-email-event.v1", emailEvent);
    }

    // 수동 처리 필요로 상태 변경
    @Transactional
    public void markAsManualProcessing(UUID id, String message) {
        Payment payment = findPayment(id);
        payment.manual_processing_required("수동 처리 필요: " + message);
        log.error("[결제 취소 수동 처리 필요] - PaymentId: {}, OrderId: {}, Reason: {}", payment.getId(), payment.getOrderId(), message);
        PaymentSendEmailEvent emailEvent = new PaymentSendEmailEvent(payment.getMemberId(), payment.getOrderId(), payment.getAmount(), payment.getStatus().toString(), payment.getFailReason());
        paymentEventHandler.saveOutbox("PaymentSendEmail", payment.getId(), "payment-service.send-email-event.v1", emailEvent);
    }

    private Payment findPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId);
        if (payment == null) {
            throw new PaymentException(PAYMENT_NOT_FOUND);
        }
        return payment;
    }
}
