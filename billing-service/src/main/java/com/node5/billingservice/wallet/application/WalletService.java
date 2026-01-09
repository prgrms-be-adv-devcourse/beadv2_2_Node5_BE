package com.node5.billingservice.wallet.application;

import com.node5.billingservice.wallet.client.TransferClient;
import com.node5.billingservice.wallet.client.dto.TransferRequset;
import com.node5.billingservice.wallet.client.dto.TransferResponse;
import com.node5.billingservice.wallet.domain.WalletDepositLog;
import com.node5.billingservice.wallet.domain.WalletDepositLogRepository;
import com.node5.billingservice.wallet.domain.WalletTransactionLog;
import com.node5.billingservice.wallet.domain.WalletTransactionLogGroupType;
import com.node5.billingservice.wallet.domain.WalletTransactionLogRepository;
import com.node5.billingservice.wallet.domain.WalletTransferLog;
import com.node5.billingservice.wallet.domain.WalletTransferLogRepository;
import com.node5.billingservice.wallet.domain.WalletWithdrawLog;
import com.node5.billingservice.wallet.domain.WalletWithdrawLogRepository;
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
import static com.node5.billingservice.wallet.domain.WalletTransactionLogStatus.COMPLETED;
import static com.node5.billingservice.wallet.domain.WalletTransactionLogStatus.REFUNDED;
import static com.node5.billingservice.wallet.domain.WalletTransactionLogType.*;
import static com.node5.billingservice.wallet.exception.WalletErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletDepositLogRepository walletDepositLogRepository;
    private final WalletWithdrawLogRepository walletWithdrawLogRepository;
    private final WalletTransferLogRepository walletTransferLogRepository;
    private final WalletTransactionLogRepository walletTransactionLogRepository;
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

    // memberId에 대한 예치금 입출금 내역 조회
    public Page<WalletLogInfo> getTransactions(UUID memberId, Pageable pageable) {
        Page<WalletTransactionLog> transactionLogPage = walletTransactionLogRepository.findAllByMemberId(memberId, pageable);
        return transactionLogPage.map(WalletLogInfo::from);
    }

    // memberId에 대한 예치금 입금 내역 조회
    public Page<WalletLogInfo> getDeposits(UUID memberId, Pageable pageable) {
        Page<WalletTransactionLog> depositLogPage = walletTransactionLogRepository.findInLogByMemberId(memberId, pageable);
        return depositLogPage.map(WalletLogInfo::from);
    }

    // memberId에 대한 예치금 출금 내역 조회
    public Page<WalletLogInfo> getWithdraws(UUID memberId, Pageable pageable) {
        Page<WalletTransactionLog> withdrawLogPage = walletTransactionLogRepository.findOutLogByMemberId(memberId, pageable);
        return withdrawLogPage.map(WalletLogInfo::from);
    }

    // 예치금 정산
    @Transactional
    public WalletSettleInfo settleWallet(UUID memberId, WalletSettleCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        if (walletDepositLogRepository.existsBySettlementId(command.settlementId())) {
            throw new WalletException(WALLET_SETTLEMENT_ALREADY_EXISTS);
        }

        wallet.deposit(command.amount());

        WalletDepositLog walletDepositLog = WalletDepositLog.builder()
                .memberId(memberId)
                .settlementId(command.settlementId())
                .amount(command.amount())
                .build();
        WalletDepositLog savedLog = walletDepositLogRepository.save(walletDepositLog);

        WalletTransactionLog transactionLog = WalletTransactionLog.builder()
                .memberId(memberId)
                .referenceId(command.settlementId().toString())
                .type(SETTLEMENT)
                .groupType(WalletTransactionLogGroupType.IN)
                .amount(command.amount())
                .balanceAfter(wallet.getBalance())
                .status(COMPLETED)
                .build();
        walletTransactionLogRepository.save(transactionLog);

        return WalletSettleInfo.from(wallet, savedLog.getCreatedAt());
    }

    //예치금 사용
    @Transactional
    public WalletInfo withdrawWallet(UUID memberId, WalletWithdrawCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        wallet.withdraw(command.withdrawAmount());

        WalletWithdrawLog walletWithdrawLog = WalletWithdrawLog.builder()
                .memberId(memberId)
                .orderId(command.orderId())
                .amount(command.withdrawAmount())
                .build();
        walletWithdrawLogRepository.save(walletWithdrawLog);

        WalletTransactionLog transactionLog = WalletTransactionLog.builder()
                .memberId(memberId)
                .referenceId(command.orderId().toString())
                .type(ORDER)
                .groupType(WalletTransactionLogGroupType.OUT)
                .amount(command.withdrawAmount())
                .balanceAfter(wallet.getBalance())
                .status(COMPLETED)
                .build();

        walletTransactionLogRepository.save(transactionLog);

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

        String transactionId = withdrawLog.getOrderId().toString();
        int update = walletTransactionLogRepository.updateStatusByTransactionId(memberId, transactionId, ORDER, COMPLETED, REFUNDED);
        if (update == 0) {
            throw new WalletException(WALLET_REFUND_INVALID_STATUS);
        }

        wallet.deposit(command.refundAmount());

        WalletTransactionLog transactionLog = WalletTransactionLog.builder()
                .memberId(memberId)
                .referenceId(transactionId)
                .type(ORDER_REFUND)
                .groupType(WalletTransactionLogGroupType.IN)
                .amount(command.refundAmount())
                .balanceAfter(wallet.getBalance())
                .status(COMPLETED)
                .build();

        walletTransactionLogRepository.save(transactionLog);

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

        wallet.withdraw(command.transferAmount());

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

        WalletTransactionLog transactionLog = WalletTransactionLog.builder()
                .memberId(memberId)
                .referenceId(response.transactionId())
                .type(TRANSFER)
                .groupType(WalletTransactionLogGroupType.OUT)
                .amount(command.transferAmount())
                .balanceAfter(wallet.getBalance())
                .status(COMPLETED)
                .build();
        walletTransactionLogRepository.save(transactionLog);

        return WalletTransferInfo.from(walletTransferLog);
    }

}
