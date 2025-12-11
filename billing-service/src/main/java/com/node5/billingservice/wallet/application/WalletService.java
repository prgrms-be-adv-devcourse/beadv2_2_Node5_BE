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

import static com.node5.billingservice.wallet.exception.WalletErrorCode.*;

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

    // 예치금 정산
    @Transactional
    public WalletInfo settleWallet(UUID memberId, WalletSettleCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        WalletDepositLog walletDepositLog = WalletDepositLog.builder()
                .memberId(memberId)
                .settlementId(command.settlementId())
                .amount(command.amount())
                .build();
        walletDepositLogRepository.save(walletDepositLog);

        wallet.deposit(command.amount());
        return WalletInfo.from(wallet);
    }

    //예치금 사용
    @Transactional
    public WalletInfo withdrawWallet(UUID memberId, WalletWithdrawCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        wallet.validateSufficientBalance(command.withdrawAmount());

        WalletWithdrawLog walletWithdrawLog = WalletWithdrawLog.builder()
                .memberId(memberId)
                .orderId(command.orderId())
                .amount(command.withdrawAmount())
                .build();
        walletWithdrawLogRepository.save(walletWithdrawLog);

        wallet.withdraw(command.withdrawAmount());
        return WalletInfo.from(wallet);
    }

    // 예치금 환불 요청
    // 예치금 환불은 PAID 상태인 출금 내역에 대해서만 요청 가능
    @Transactional
    public WalletInfo refundWallet(UUID memberId, WalletRefundCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        WalletWithdrawLog withdrawLog = walletWithdrawLogRepository.findByOrderId(command.orderId())
                .orElseThrow(() -> new WalletException(WALLET_WITHDRAW_LOG_NOT_FOUND));

        withdrawLog.validateRefundable(command.orderId(), command.refundAmount());
        withdrawLog.refund();

        wallet.deposit(withdrawLog.getAmount());
        return WalletInfo.from(wallet);
    }
}
