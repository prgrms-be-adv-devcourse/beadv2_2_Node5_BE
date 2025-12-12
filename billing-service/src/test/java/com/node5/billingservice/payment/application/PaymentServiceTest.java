package com.node5.billingservice.payment.application;

import com.node5.billingservice.IntegrationTestSupport;
import com.node5.billingservice.payment.application.dto.*;
import com.node5.billingservice.payment.client.TossPaymentClient;
import com.node5.billingservice.payment.client.dto.TossPaymentResponse;
import com.node5.billingservice.payment.domain.Payment;
import com.node5.billingservice.payment.domain.PaymentStatus;
import com.node5.billingservice.payment.infrastructure.PaymentRepositoryAdapter;
import com.node5.billingservice.wallet.domain.Wallet;
import com.node5.billingservice.wallet.exception.WalletException;
import com.node5.billingservice.wallet.infrastructure.WalletRepositoryAdapter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.node5.billingservice.wallet.exception.WalletErrorCode.WALLET_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

public class PaymentServiceTest extends IntegrationTestSupport {

    @Autowired
    private PaymentService paymentService;

    @MockitoBean
    private PaymentRepositoryAdapter paymentRepositoryAdapter;

    @MockitoBean
    private WalletRepositoryAdapter walletRepositoryAdapter;

    @MockitoBean
    private TossPaymentClient tossPaymentClient;

    @Test
    @DisplayName("결제 요청을 처리한다. 요청이 성공적으로 처리되면 결제 정보를 반환한다.")
    void processPaymentRequest() {
        // given
        UUID memberId = UUID.randomUUID();
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .build();
        UUID walletID = wallet.getId();
        Payment payment = Payment.builder()
                .walletId(walletID)
                .amount(1000L)
                .build();
        when(walletRepositoryAdapter.findByMemberId(memberId))
                .thenReturn(Optional.of(wallet));
        when(paymentRepositoryAdapter.save(payment))
                .thenReturn(payment);
        PaymentCommand command = new PaymentCommand(
                1000L
        );

        // when
        PaymentInfo paymentInfo = paymentService.request(memberId, command);

        // then
        assertThat(paymentInfo)
                .extracting("walletId", "amount", "status")
                .contains(walletID, 1000L, PaymentStatus.READY);
    }

    @Test
    @DisplayName("결제 내역을 조회할 때, 해당 멤버의 지갑이 존재하지 않으면 예외가 발생한다.")
    void testFindAllPayments_WalletNotFound() {
        // given
        UUID memberId = UUID.randomUUID();
        when(walletRepositoryAdapter.findByMemberId(memberId))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.request(memberId, null))
                .isInstanceOfSatisfying(WalletException.class, ex -> {
                    Assertions.assertThat(ex.getErrorCode())
                            .isEqualTo(WALLET_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("결제가 승인을 처리한다. 승인이 성공적으로 처리되면 결제 정보를 반환한다.")
    void processPaymentConfirmation() {
        // given
        UUID memberId = UUID.randomUUID();
        String paymentKey = "paymentKey";
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .build();
        UUID walletID = wallet.getId();
        Payment payment = Payment.builder()
                .walletId(walletID)
                .amount(1000L)
                .build();
        String orderId = payment.getOrderId();
        TossPaymentResponse response = new TossPaymentResponse(
                paymentKey,
                orderId,
                1000L,
                "card",
                "status",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        PaymentConfirmCommand command = new PaymentConfirmCommand(
                paymentKey,
                orderId,
                1000L
        );
        when(walletRepositoryAdapter.findByMemberIdForUpdate(memberId))
                .thenReturn(Optional.of(wallet));
        when(paymentRepositoryAdapter.findByOrderId(orderId))
                .thenReturn(Optional.of(payment));
        when(tossPaymentClient.confirm(command))
                .thenReturn(response);

        // when
        PaymentInfo paymentInfo = paymentService.confirm(memberId, command);

        // then
        assertThat(paymentInfo)
                .extracting("walletId", "paymentKey", "amount", "status")
                .contains(walletID, paymentKey, 1000L, PaymentStatus.CONFIRMED);
    }

    @Test
    @DisplayName("결제 실패를 처리한다. 실패가 성공적으로 처리되면 결제 실패 정보를 반환한다.")
    void processPaymentFailure() {
        UUID memberId = UUID.randomUUID();
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .build();
        UUID walletID = wallet.getId();
        Payment payment = Payment.builder()
                .walletId(walletID)
                .amount(1000L)
                .build();
        String orderId = payment.getOrderId();
        when(walletRepositoryAdapter.findByMemberIdForUpdate(memberId))
                .thenReturn(Optional.of(wallet));
        when(paymentRepositoryAdapter.findByOrderId(orderId))
                .thenReturn(Optional.of(payment));
        PaymentFailureCommand command = new PaymentFailureCommand(
        orderId,
        "ERROR_CODE",
        "Error occurred",
        1000L,
        "rawPayload"
        );
        // when
        PaymentFailureInfo failureInfo = paymentService.failure(memberId, command);
        // then
        assertThat(failureInfo)
                .extracting("orderId", "amount", "errorCode", "errorMessage")
                .contains(orderId, 1000L, "ERROR_CODE", "Error occurred");

    }

    @Test
    @DisplayName("결제 취소를 처리한다. 취소가 성공적으로 처리되면 결제 정보를 반환한다.")
    void processPaymentCancellation() {
        UUID memberId = UUID.randomUUID();
        String paymentKey = "paymentKey";
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .build();
        UUID walletID = wallet.getId();
        when(walletRepositoryAdapter.findByMemberIdForUpdate(memberId))
                .thenReturn(Optional.of(wallet));
        Payment payment = Payment.builder()
                .walletId(walletID)
                .amount(1000L)
                .build();
        String orderId = payment.getOrderId();
        when(paymentRepositoryAdapter.findByOrderId(orderId))
                .thenReturn(Optional.of(payment));
        TossPaymentResponse response = new TossPaymentResponse(
                paymentKey,
                orderId,
                1000L,
                "card",
                "status",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        payment.confirm(response);
        wallet.deposit(1000L);
        PaymentCancelCommand command = new PaymentCancelCommand(
                paymentKey,
                orderId,
                1000L
        );
        when(tossPaymentClient.cancel(command))
                .thenReturn(response);
        // when
        PaymentInfo paymentInfo = paymentService.cancel(memberId, command);
        // then
        assertThat(paymentInfo)
                .extracting("walletId", "paymentKey", "amount", "status")
                .contains(walletID, paymentKey, 1000L, PaymentStatus.CANCELED);

    }

}
