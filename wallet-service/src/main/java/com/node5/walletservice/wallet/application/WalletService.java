package com.node5.walletservice.wallet.application;

import com.node5.common.event.PaymentDepositEvent;
import com.node5.walletservice.wallet.client.TransferClient;
import com.node5.walletservice.wallet.client.dto.TransferRequset;
import com.node5.walletservice.wallet.client.dto.TransferResponse;
import com.node5.walletservice.wallet.domain.WalletDepositLog;
import com.node5.walletservice.wallet.domain.WalletDepositLogRepository;
import com.node5.walletservice.wallet.domain.WalletTransactionLog;
import com.node5.walletservice.wallet.domain.WalletTransactionLogGroupType;
import com.node5.walletservice.wallet.domain.WalletTransactionLogRepository;
import com.node5.walletservice.wallet.domain.WalletTransferLog;
import com.node5.walletservice.wallet.domain.WalletTransferLogRepository;
import com.node5.walletservice.wallet.domain.WalletWithdrawLog;
import com.node5.walletservice.wallet.domain.WalletWithdrawLogRepository;
import com.node5.walletservice.wallet.exception.WalletException;
import com.node5.walletservice.wallet.application.dto.*;
import com.node5.walletservice.wallet.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.node5.walletservice.wallet.client.TransferStateCode.*;
import static com.node5.walletservice.wallet.domain.WalletTransactionLogStatus.*;
import static com.node5.walletservice.wallet.domain.WalletTransactionLogType.*;
import static com.node5.walletservice.wallet.exception.WalletErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
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

    @Transactional
    public void depositRequest(PaymentDepositEvent event) {
        if (walletTransactionLogRepository.existsLog(event.memberId(), event.orderId(), COMPLETED)) {
            log.info("이미 완료된 입금 요청입니다. 건너뜁니다. orderId: {}", event.orderId());
            return;
        }

        Wallet wallet = walletRepository.findByMemberIdForUpdate(event.memberId())
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        wallet.deposit(event.amount());

        try {
            WalletTransactionLog transactionLog = WalletTransactionLog.builder()
                    .memberId(event.memberId())
                    .referenceId(event.orderId())
                    .type(CHARGE)
                    .groupType(WalletTransactionLogGroupType.IN)
                    .amount(event.amount())
                    .balanceAfter(wallet.getBalance())
                    .status(COMPLETED)
                    .build();
            walletTransactionLogRepository.save(transactionLog);
        } catch (DataIntegrityViolationException e) {
            log.warn("중복된 트랜잭션 로그 삽입 시도 감지. 롤백 처리됩니다. orderId: {}", event.orderId());
            throw new WalletException(WALLET_DUPLICATE_DEPOSIT_REQUEST); // 롤백을 유도하거나 상황에 따라 처리
        }
    }

    // 예치금 결제 취소 요청
    @Transactional
    public void withdrawRequest(WalletPaymentCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(command.memberId())
                .orElseThrow(() -> new WalletException(WALLET_NOT_FOUND));

        wallet.withdraw(command.amount());

        walletTransactionLogRepository.updateStatusByTransactionId(command.memberId(), command.orderId(), CHARGE, COMPLETED, CANCELED);

        WalletTransactionLog transactionLog = WalletTransactionLog.builder()
                .memberId(command.memberId())
                .referenceId(command.orderId())
                .type(CHARGE_CANCEL)
                .groupType(WalletTransactionLogGroupType.OUT)
                .amount(command.amount())
                .balanceAfter(wallet.getBalance())
                .status(COMPLETED)
                .build();
        walletTransactionLogRepository.save(transactionLog);
    }
}
