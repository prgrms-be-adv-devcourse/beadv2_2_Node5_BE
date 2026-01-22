package com.node5.paymentservice.payment.application;

import com.node5.paymentservice.payment.client.openfeign.WalletClient;
import com.node5.paymentservice.payment.client.openfeign.dto.WalletRequest;
import com.node5.paymentservice.payment.domain.Payment;
import com.node5.paymentservice.payment.domain.PaymentRepository;
import com.node5.paymentservice.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.node5.paymentservice.payment.exception.PaymentErrorCode.PAYMENT_WALLET_WITHDRAW_FAILED;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCancelWithdrawService {

    private final PaymentRepository paymentRepository;
    private final WalletClient walletClient;

    @Transactional
    public void cancelWithdrawPaymentProcessing(Payment payment) {        // 예치금 출금 요청
        try {
            WalletRequest withdrawRequest = new WalletRequest(
                    payment.getMemberId(),
                    payment.getOrderId(),
                    payment.getAmount()
            );
            walletClient.withdrawRequest(withdrawRequest);
            payment.withdraw_confirmed();
            paymentRepository.saveAndFlush(payment);
        } catch (Exception e) {
            log.error("[예치금 출금 요청 실패] 결제 취소 실패 처리 - MemberId: {}, OrderId: {}, Error: {}", payment.getMemberId(), payment.getOrderId(), e.getMessage());
            throw new PaymentException(PAYMENT_WALLET_WITHDRAW_FAILED); //예치금 출금 요청 실패
        }
    }
}
