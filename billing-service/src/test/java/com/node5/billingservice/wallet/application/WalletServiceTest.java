package com.node5.billingservice.wallet.application;

import com.node5.billingservice.IntegrationTestSupport;
import com.node5.billingservice.wallet.application.dto.WalletInfo;
import com.node5.billingservice.wallet.application.dto.WalletRefundCommand;
import com.node5.billingservice.wallet.application.dto.WalletSettleCommand;
import com.node5.billingservice.wallet.application.dto.WalletWithdrawCommand;
import com.node5.billingservice.wallet.domain.Wallet;
import com.node5.billingservice.wallet.domain.WalletWithdrawLog;
import com.node5.billingservice.wallet.exception.WalletException;
import com.node5.billingservice.wallet.infrastructure.WalletRepositoryAdapter;
import com.node5.billingservice.wallet.infrastructure.WalletWithdrawLogRepositoryAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.node5.billingservice.wallet.exception.WalletErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@Transactional
public class WalletServiceTest extends IntegrationTestSupport {

    @Autowired
    private WalletService walletService;

    @MockitoBean
    private WalletRepositoryAdapter walletRepositoryAdapter;

    @MockitoBean
    private WalletWithdrawLogRepositoryAdapter walletWithdrawLogRepositoryAdapter;

    @Test
    @DisplayName("예치금을 생성한다. memberId로 예치금을 생성하면 예치금 정보가 반환된다.")
    public void createWallet() {
        //given
        UUID memberId = UUID.randomUUID();

        //when
        WalletInfo walletInfo = walletService.createWallet(memberId);

        //then
        assertThat(walletInfo)
                .extracting("memberId", "balance")
                .contains(memberId, 0L);
    }

    @Test
    @DisplayName("예치금을 생성할 때, 이미 존재한다면 예외가 발생한다.")
    public void createWallet_AlreadyExists() {
        //given
        UUID memberId = UUID.randomUUID();
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .build();
        when(walletRepositoryAdapter.findByMemberId(memberId))
                .thenReturn(Optional.of(wallet));

        //when then
        assertThatThrownBy(() -> walletService.createWallet(memberId))
                .isInstanceOfSatisfying(WalletException.class, ex -> {
                    assertThat(ex.getErrorCode())
                            .isEqualTo(WALLET_ALREADY_EXISTS);
                });
    }

    @Test
    @DisplayName("예치금을 정산한다. memberId로 예치금을 정산하면 예치금 정보가 반환된다.")
    public void settleWallet() {
        //given
        UUID memberId = UUID.randomUUID();
        Long amount = 1000L;
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .build();
        when(walletRepositoryAdapter.findByMemberIdForUpdate(memberId))
                .thenReturn(Optional.of(wallet));
        WalletSettleCommand command = new WalletSettleCommand(
                UUID.randomUUID(),
                amount
        );

        //when
        WalletInfo walletInfo = walletService.settleWallet(memberId, command);

        //then
        assertThat(walletInfo)
                .extracting("memberId", "balance")
                .contains(memberId, amount);
    }

    @Test
    @DisplayName("예치금을 정산받을 때, memberId에 해당하는 예치금이 없다면 예외가 발생한다.")
    public void settleWallet_WalletNotFound() {
        //given
        UUID memberId = UUID.randomUUID();

        //when then
        assertThatThrownBy(() -> walletService.settleWallet(memberId, null))
                .isInstanceOfSatisfying(WalletException.class, ex -> {
                    assertThat(ex.getErrorCode())
                            .isEqualTo(WALLET_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("예치금을 차감한다. memberId로 예치금을 차감하면 예치금 정보가 반환된다.")
    public void withdrawWallet() {
        //given
        UUID memberId = UUID.randomUUID();
        Long initialBalance = 2000L;
        Long withdrawAmount = 1000L;
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .build();
        wallet.deposit(initialBalance);
        when(walletRepositoryAdapter.findByMemberIdForUpdate(memberId))
                .thenReturn(Optional.of(wallet));
        WalletWithdrawCommand command = new WalletWithdrawCommand(
                UUID.randomUUID(),
                withdrawAmount
        );

        //when
        WalletInfo walletInfo = walletService.withdrawWallet(memberId, command);

        //then
        assertThat(walletInfo)
                .extracting("memberId", "balance")
                .contains(memberId, initialBalance - withdrawAmount);
    }

    @Test
    @DisplayName("예치금을 차감할 때, memberId에 해당하는 예치금이 없다면 예외가 발생한다.")
    public void withdrawWallet_WalletNotFound() {
        //given
        UUID memberId = UUID.randomUUID();

        //when then
        assertThatThrownBy(() -> walletService.withdrawWallet(memberId, null))
                .isInstanceOfSatisfying(WalletException.class, ex -> {
                    assertThat(ex.getErrorCode())
                            .isEqualTo(WALLET_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("예치금을 차감할 때, 잔액이 부족하면 예외가 발생한다.")
    public void withdrawWallet_InsufficientBalance() {
        //given
        UUID memberId = UUID.randomUUID();
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .build();
        wallet.deposit(100L);
        WalletWithdrawCommand command = new WalletWithdrawCommand(
                UUID.randomUUID(),
                1000L
        );
        when(walletRepositoryAdapter.findByMemberIdForUpdate(memberId))
                .thenReturn(Optional.of(wallet));

        //when then
        assertThatThrownBy(() -> walletService.withdrawWallet(memberId, command))
                .isInstanceOfSatisfying(WalletException.class, ex -> {
                            assertThat(ex.getErrorCode())
                                    .isEqualTo(INSUFFICIENT_WALLET_BALANCE);
                        });
    }

    @Test
    @DisplayName("예치금을 환불한다. memberId로 예치금을 환불하면 예치금 정보가 반환된다.")
    public void refundWallet() {
        //given
        UUID memberId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Long amount = 1000L;
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .build();
        WalletWithdrawLog withdrawLog = WalletWithdrawLog.builder()
                .memberId(memberId)
                .orderId(orderId)
                .amount(amount)
                .build();
        when(walletRepositoryAdapter.findByMemberIdForUpdate(memberId))
                .thenReturn(Optional.of(wallet));
        when(walletWithdrawLogRepositoryAdapter.findByOrderId(orderId))
                .thenReturn(Optional.of(withdrawLog));
        WalletRefundCommand command = new WalletRefundCommand(
                orderId,
                amount
        );

        //when
        WalletInfo walletInfo = walletService.refundWallet(memberId, command);

        //then
        assertThat(walletInfo)
                .extracting("memberId", "balance")
                .contains(memberId, amount);
    }

    @Test
    @DisplayName("예치금을 환불할 때, orderId에 해당하는 출금 내역이 없다면 예외가 발생한다.")
    public void refundWallet_WithdrawLogNotFound() {
        //given
        UUID memberId = UUID.randomUUID();
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .build();
        WalletRefundCommand command = new WalletRefundCommand(
                UUID.randomUUID(),
                1000L
        );
        when(walletRepositoryAdapter.findByMemberIdForUpdate(memberId))
                .thenReturn(Optional.of(wallet));

        //when then
        assertThatThrownBy(() -> walletService.refundWallet(memberId, command))
                .isInstanceOfSatisfying(WalletException.class, ex -> {
                    assertThat(ex.getErrorCode())
                            .isEqualTo(WALLET_WITHDRAW_LOG_NOT_FOUND);
                });
    }

    @Test
    @DisplayName("예치금을 환불할 때, 예치금 출금 상태가 PAID가 아니라면 예외가 발생한다.")
    public void refundWallet_WithdrawLogNotPaid() {
        //given
        UUID memberId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Long amount = 1000L;
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .build();
        WalletWithdrawLog withdrawLog = WalletWithdrawLog.builder()
                .memberId(memberId)
                .orderId(orderId)
                .amount(amount)
                .build();
        withdrawLog.refund();
        WalletRefundCommand command = new WalletRefundCommand(
                orderId,
                amount
        );
        when(walletRepositoryAdapter.findByMemberIdForUpdate(memberId))
                .thenReturn(Optional.of(wallet));
        when(walletWithdrawLogRepositoryAdapter.findByOrderId(orderId))
                .thenReturn(Optional.of(withdrawLog));

        //when then
        assertThatThrownBy(() -> walletService.refundWallet(memberId, command))
                .isInstanceOfSatisfying(WalletException.class, ex -> {
                    assertThat(ex.getErrorCode())
                            .isEqualTo(WALLET_REFUND_STATE_INVALID);
                });
    }

    @Test
    @DisplayName("예치금을 환불할 때, 주문id가 일치하지 않으면 예외가 발생한다.")
    public void refundWallet_OrderIdMismatch() {
        //given
        UUID memberId = UUID.randomUUID();
        UUID storedOrderId = UUID.randomUUID();
        UUID requestOrderId = UUID.randomUUID();
        Long amount = 1000L;
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .build();
        WalletWithdrawLog withdrawLog = WalletWithdrawLog.builder()
                .memberId(memberId)
                .orderId(storedOrderId)
                .amount(amount)
                .build();
        WalletRefundCommand command = new WalletRefundCommand(
                requestOrderId,
                amount
        );
        when(walletRepositoryAdapter.findByMemberIdForUpdate(memberId))
                .thenReturn(Optional.of(wallet));
        when(walletWithdrawLogRepositoryAdapter.findByOrderId(requestOrderId))
                .thenReturn(Optional.of(withdrawLog));

        //when then
        assertThatThrownBy(() -> walletService.refundWallet(memberId, command))
                .isInstanceOfSatisfying(WalletException.class, ex -> {
                    assertThat(ex.getErrorCode())
                            .isEqualTo(WALLET_ORDER_ID_MISMATCH);
                });
    }

    @Test
    @DisplayName("예치금을 환불할 때, 환불 금액이 일치하지 않으면 예외가 발생한다.")
    public void refundWallet_RefundAmountInvalid() {
        //given
        UUID memberId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Long storedAmount = 1000L;
        Long requestAmount = 2000L;
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .build();
        WalletWithdrawLog withdrawLog = WalletWithdrawLog.builder()
                .memberId(memberId)
                .orderId(orderId)
                .amount(storedAmount)
                .build();
        WalletRefundCommand command = new WalletRefundCommand(
                orderId,
                requestAmount
        );
        when(walletRepositoryAdapter.findByMemberIdForUpdate(memberId))
                .thenReturn(Optional.of(wallet));
        when(walletWithdrawLogRepositoryAdapter.findByOrderId(orderId))
                .thenReturn(Optional.of(withdrawLog));

        //when then
        assertThatThrownBy(() -> walletService.refundWallet(memberId, command))
                .isInstanceOfSatisfying(WalletException.class, ex -> {
                    assertThat(ex.getErrorCode())
                            .isEqualTo(WALLET_REFUND_AMOUNT_INVALID);
                });
    }

    @Test
    @DisplayName("예치금을 삭제한다. memberId로 예치금을 삭제하면 예치금이 삭제된다.")
    public void deleteWallet() {
        //given
        UUID memberId = UUID.randomUUID();
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .build();
        when(walletRepositoryAdapter.findByMemberIdForUpdate(memberId))
                .thenReturn(Optional.of(wallet));

        //when
        walletService.deleteWallet(memberId);

        //then
        assertThat(wallet.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("예치금을 삭제할 때, memberId에 해당하는 예치금이 없다면 예외가 발생한다.")
    public void deleteWallet_WalletNotFound() {
        //given
        UUID memberId = UUID.randomUUID();

        //when then
        assertThatThrownBy(() -> walletService.deleteWallet(memberId))
                .isInstanceOfSatisfying(WalletException.class, ex -> {
                    assertThat(ex.getErrorCode())
                            .isEqualTo(WALLET_NOT_FOUND);
                });
    }

}
