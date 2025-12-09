package com.node5.billingservice.wallet.application;

import com.node5.billingservice.wallet.exception.WalletException;
import com.node5.billingservice.wallet.application.dto.*;
import com.node5.billingservice.wallet.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.node5.billingservice.wallet.domain.WalletDepositLogState.*;
import static com.node5.billingservice.wallet.exception.WalletErrorCode.WALLET_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletDepositLogRepository walletDepositLogRepository;
    private final WalletWithdrawLogRepository walletWithdrawLogRepository;

    // memberId에 대한 예치금 조회
    public WalletInfo getWallet(UUID memberId) {
        Wallet wallet = walletRepository.findByMemberId(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));
        return WalletInfo.from(wallet);
    }

    // memberId로 예치금 생성
    @Transactional
    public WalletInfo createWallet(UUID memberId) {
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .build();
        walletRepository.save(wallet);
        return WalletInfo.from(wallet);
    }

    // memberId에 대한 예치금 입금 내역 조회
    public Page<WalletDepositInfo> getDeposits(UUID memberId, Pageable pageable) {
        Page<WalletDepositLog> depositLogPage = walletDepositLogRepository.findAllByMemberId(memberId, pageable);
        return depositLogPage.map(WalletDepositInfo::from);
    }

    // memberId에 대한 예치금 출금 내역 조회
    public Page<WalletWithdrawInfo> getWithdraws(UUID memberId, Pageable pageable) {
        Page<WalletWithdrawLog> withdrawLogPage = walletWithdrawLogRepository.findAllByMemberId(memberId, pageable);
        return withdrawLogPage.map(WalletWithdrawInfo::from);
    }

    // 예치금 충전 (내부 api로 수정 예정)
    @Transactional
    public WalletInfo chargeWallet(UUID memberId, WalletChargeCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for memberId: " + memberId));
        wallet.deposit(command.amount());

        WalletDepositLog walletDepositLog = WalletDepositLog.paidBuilder()
                .memberId(memberId)
                .paymentKey(command.paymentKey())
                .amount(command.amount())
                .build();
        walletDepositLogRepository.save(walletDepositLog);
        return WalletInfo.from(wallet);
    }

    // 예치금 정산 (내부 api로 수정 예정)
    @Transactional
    public WalletInfo settleWallet(UUID memberId, WalletSettleCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));
        wallet.deposit(command.amount());

        WalletDepositLog walletDepositLog = WalletDepositLog.settledBuilder()
                .memberId(memberId)
                .amount(command.amount())
                .build();
        walletDepositLogRepository.save(walletDepositLog);
        return WalletInfo.from(wallet);
    }

    //예치금 사용 (내부 api로 수정 예정)
    @Transactional
    public WalletInfo withdrawWallet(UUID memberId, WalletWithdrawCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        if (wallet.getBalance() < command.amount()) {
            throw new IllegalArgumentException("요청한 가격보다 예치금이 부족합니다.");
        }

        wallet.withdraw(command.amount());

        WalletWithdrawLog walletWithdrawLog = WalletWithdrawLog.builder()
                .memberId(memberId)
                .amount(command.amount())
                .build();
        walletWithdrawLogRepository.save(walletWithdrawLog);
        return WalletInfo.from(wallet);
    }

    // 예치금 환불 요청 (내부 api로 수정 예정)
    // 예치금 환불은 PAID 상태인 입금 내역에 대해서만 요청 가능
    @Transactional
    public WalletInfo requestRefundWallet(UUID memberId, WalletRefundCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for memberId: " + memberId));

        WalletDepositLog walletDepositLog = walletDepositLogRepository.findById(command.walletDepositLogId())
                .orElseThrow(() -> new IllegalArgumentException("WalletDepositLog not found for id: " + command.walletDepositLogId()));

        if (walletDepositLog.getState() != PAID) {
            throw new IllegalArgumentException("환불 가능한 상태가 아닙니다.");
        }

        if (walletDepositLog.getPaymentKey() == null) {
            throw new IllegalArgumentException("결제 키가 존재하지 않습니다.");
        }

        if (!walletDepositLog.getPaymentKey().equals(command.paymentKey())) {
            throw new IllegalArgumentException("결제 키가 일치하지 않습니다.");
        }

        if (wallet.getBalance() < walletDepositLog.getAmount()) {
            throw new IllegalArgumentException("환불할 예치금이 부족합니다.");
        }

        wallet.withdraw(walletDepositLog.getAmount());
        walletDepositLog.changeState(CANCEL_WAITING);
        return WalletInfo.from(wallet);
    }

    // 예치금 환불 성공 (내부 api로 수정 예정)
    @Transactional
    public WalletInfo confirmRefundWallet(UUID memberId, WalletRefundCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        WalletDepositLog walletDepositLog = walletDepositLogRepository.findById(command.walletDepositLogId())
                .orElseThrow(() -> new IllegalArgumentException("WalletDepositLog not found for id: " + command.walletDepositLogId()));

        if (walletDepositLog.getState() != CANCEL_WAITING) {
            throw new IllegalArgumentException("환불 가능한 상태가 아닙니다.");
        }

        if (walletDepositLog.getPaymentKey() == null) {
            throw new IllegalArgumentException("결제 키가 존재하지 않습니다.");
        }

        if (!walletDepositLog.getPaymentKey().equals(command.paymentKey())) {
            throw new IllegalArgumentException("결제 키가 일치하지 않습니다.");
        }

        walletDepositLog.changeState(CANCELED);
        return WalletInfo.from(wallet);
    }

    // 예치금 환불 실패 (내부 api로 수정 예정)
    @Transactional
    public WalletInfo failRefundWallet(UUID memberId, WalletRefundCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        WalletDepositLog walletDepositLog = walletDepositLogRepository.findById(command.walletDepositLogId())
                .orElseThrow(() -> new IllegalArgumentException("WalletDepositLog not found for id: " + command.walletDepositLogId()));

        if (walletDepositLog.getState() != CANCEL_WAITING) {
            throw new IllegalArgumentException("환불 가능한 상태가 아닙니다.");
        }

        if (walletDepositLog.getPaymentKey() == null) {
            throw new IllegalArgumentException("결제 키가 존재하지 않습니다.");
        }

        if (!walletDepositLog.getPaymentKey().equals(command.paymentKey())) {
            throw new IllegalArgumentException("결제 키가 일치하지 않습니다.");
        }

        wallet.deposit(walletDepositLog.getAmount());
        walletDepositLog.changeState(PAID);
        return WalletInfo.from(wallet);
    }
}
