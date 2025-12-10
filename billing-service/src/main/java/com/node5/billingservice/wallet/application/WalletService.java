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
import static com.node5.billingservice.wallet.exception.WalletErrorCode.WALLET_LOG_NOT_FOUND;
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
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));
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

        wallet.validateSufficientBalance(command.amount());

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
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        WalletDepositLog depositLog = walletDepositLogRepository.findById(command.walletDepositLogId())
                .orElseThrow(() -> new WalletException(WALLET_LOG_NOT_FOUND));

        depositLog.validateRefundable(command.paymentKey(), PAID);

        wallet.validateSufficientBalance(depositLog.getAmount());

        wallet.withdraw(depositLog.getAmount());
        depositLog.changeState(CANCEL_WAITING);
        return WalletInfo.from(wallet);
    }

    // 예치금 환불 성공 (내부 api로 수정 예정)
    @Transactional
    public WalletInfo confirmRefundWallet(UUID memberId, WalletRefundCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        WalletDepositLog depositLog = walletDepositLogRepository.findById(command.walletDepositLogId())
                .orElseThrow(() -> new WalletException(WALLET_LOG_NOT_FOUND));

        depositLog.validateRefundable(command.paymentKey(), CANCEL_WAITING);
        depositLog.changeState(CANCELED);
        return WalletInfo.from(wallet);
    }

    // 예치금 환불 실패 (내부 api로 수정 예정)
    @Transactional
    public WalletInfo failRefundWallet(UUID memberId, WalletRefundCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        WalletDepositLog depositLog = walletDepositLogRepository.findById(command.walletDepositLogId())
                .orElseThrow(() -> new WalletException(WALLET_LOG_NOT_FOUND));

        depositLog.validateRefundable(command.paymentKey(), CANCEL_WAITING);

        wallet.deposit(depositLog.getAmount());
        depositLog.changeState(PAID);
        return WalletInfo.from(wallet);
    }
}
