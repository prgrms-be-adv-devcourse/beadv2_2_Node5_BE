package com.node5.billingservice.wallet.application;

import com.node5.billingservice.wallet.client.TransferClient;
import com.node5.billingservice.wallet.client.dto.TransferRequset;
import com.node5.billingservice.wallet.client.dto.TransferResponse;
import com.node5.billingservice.wallet.exception.WalletException;
import com.node5.billingservice.wallet.application.dto.*;
import com.node5.billingservice.wallet.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.node5.billingservice.wallet.client.TransferStateCode.*;
import static com.node5.billingservice.wallet.exception.WalletErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletDepositLogRepository walletDepositLogRepository;
    private final WalletWithdrawLogRepository walletWithdrawLogRepository;
    private final WalletTransferLogRepository walletTransferLogRepository;
    private final WalletDeletedProducer walletDeletedProducer;
    private final TransferClient transferClient;

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
        WalletDepositLog savedLog = walletDepositLogRepository.save(walletDepositLog);

        wallet.deposit(command.amount());
        return WalletSettleInfo.from(wallet, savedLog.getCreatedAt());
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

        wallet.delete();
    }

    //예치금 이체
    @Transactional
    public WalletTransferInfo transferWallet(UUID memberId, WalletTransferCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        wallet.validateSufficientBalance(command.transferAmount());

        String orderId = "ORDER-" + UUID.randomUUID();

        TransferResponse response = transferClient.executeTransfer(
                new TransferRequset(command.toAccountNo(), command.transferAmount(), orderId)
        );
        if (!response.isSuccess()) {
            if (response.stateCode().equals(BANK_TIMEOUT)) {
                throw new WalletException(WALLET_TRANSFER_BANK_TIMEOUT);
            } else if (response.stateCode().equals(BANK_MAINTENANCE)) {
                throw new WalletException(WALLET_TRANSFER_BANK_MAINTENANCE);
            } else if (response.stateCode().equals(INVALID_ACCOUNT)) {
                throw new WalletException(WALLET_TRANSFER_INVALID_ACCOUNT);
            } else {
                throw new WalletException(WALLET_TRANSFER_SYSTEM_ERROR);
            }
        }

        WalletTransferLog walletTransferLog = WalletTransferLog.builder()
                .memberId(memberId)
                .accountNo(command.toAccountNo())
                .amount(command.transferAmount())
                .transactionId(response.transactionId())
                .message(response.message())
                .requestedAt(response.requestedAt())
                .approvedAt(response.completedAt())
                .build();
        walletTransferLogRepository.save(walletTransferLog);

        wallet.withdraw(command.transferAmount());
        return WalletTransferInfo.from(walletTransferLog);
    }

    // memberId에 대한 예치금 이체 내역 조회
    public Page<WalletTransferInfo> getTransfers(UUID memberId, Pageable pageable) {
        Page<WalletTransferLog> transferLogPage = walletTransferLogRepository.findAllByMemberId(memberId, pageable);
        return transferLogPage.map(WalletTransferInfo::from);
    }
}
