package com.node5.billingservice.wallet.application;

import com.node5.billingservice.wallet.exception.WalletException;
import com.node5.billingservice.wallet.application.dto.*;
import com.node5.billingservice.wallet.domain.*;
import com.node5.billingservice.wallet.infrastructure.kafka.producer.WalletDeletedProducer;
import com.node5.common.event.WalletDeletedEvent;
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
    private final WalletDeletedProducer walletDeletedProducer;

    // memberId에 대한 예치금 조회
    public WalletInfo getWallet(UUID memberId) {
        Wallet wallet = walletRepository.findByMemberId(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));
        return WalletInfo.from(wallet);
    }

    // memberId로 예치금 생성
    @Transactional
    public WalletInfo createWallet(UUID memberId) {

        if (walletRepository.findByMemberId(memberId).isPresent()) {
            throw new WalletException(WALLET_ALREADY_EXISTS);
        }

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
    public WalletSettleInfo settleWallet(UUID memberId, WalletSettleCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        if (walletDepositLogRepository.existsBySettlementId(command.settlementId())) {
            throw new WalletException(WALLET_SETTLEMENT_ALREADY_EXISTS);
        }

        WalletDepositLog walletDepositLog = WalletDepositLog.builder()
                .memberId(memberId)
                .settlementId(command.settlementId())
                .amount(command.amount())
                .build();
        walletDepositLogRepository.save(walletDepositLog);

        wallet.deposit(command.amount());
        return WalletSettleInfo.from(wallet, walletDepositLog.getCreatedAt());
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

    //예치금 삭제
    @Transactional
    public void deleteWallet(UUID memberId) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        WalletDeletedEvent walletDeletedEvent = new WalletDeletedEvent(wallet.getId());

        wallet.delete();
        //TODO - 연관된 입출금 내역도 삭제할지 고민
        //TODO - 지갑이 삭제 되었는지 확인하는 쿼리 필요
        // 지갑 삭제 topic 발행
        walletDeletedProducer.send(walletDeletedEvent);
    }
}
