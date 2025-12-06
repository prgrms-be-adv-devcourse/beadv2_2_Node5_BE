package com.node5.walletservice.wallet.application;

import com.node5.common.domain.ApiResponseDto;
import com.node5.common.domain.PageInfoDto;
import com.node5.common.domain.PagedResponseDto;
import com.node5.walletservice.wallet.application.dto.*;
import com.node5.walletservice.wallet.domain.*;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.node5.walletservice.wallet.domain.WalletDepositLogState.*;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;

    private final WalletDepositLogRepository walletDepositLogRepository;
    private final WalletWithdrawLogRepository walletWithdrawLogRepository;

    // memberId에 대한 예치금 조회
    @Transactional(readOnly = true)
    public ApiResponseDto<WalletInfo> getWallet(UUID memberId) {
        Wallet wallet = walletRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for memberId: " + memberId));
        return new ApiResponseDto<>(HttpStatus.OK.value(), HttpStatus.OK.name(), WalletInfo.from(wallet));
    }

    // memberId로 예치금 생성
    public ApiResponseDto<WalletInfo> createWallet(UUID memberId) {
        Wallet wallet = Wallet.builder()
                .memberId(memberId)
                .build();
        Wallet savedWallet = walletRepository.save(wallet);
        return new ApiResponseDto<>(HttpStatus.CREATED.value(), HttpStatus.CREATED.name(), WalletInfo.from(savedWallet));
    }

    // memberId에 대한 예치금 입금 내역 조회
    public PagedResponseDto<WalletDepositInfo> getDeposits(UUID memberId, Pageable pageable) {
        Page<WalletDepositLog> page = walletDepositLogRepository.findAllByMemberId(memberId, pageable);
        List<WalletDepositInfo> walletDepositLogs = page.stream()
                .map(WalletDepositInfo::from)
                .toList();
        PageInfoDto pageInfo = new PageInfoDto(page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
        return new PagedResponseDto<>(walletDepositLogs, pageInfo);
    }

    // memberId에 대한 예치금 출금 내역 조회
    public PagedResponseDto<WalletWithdrawInfo> getWithdraws(UUID memberId, Pageable pageable) {
        Page<WalletWithdrawLog> page = walletWithdrawLogRepository.findAllByMemberId(memberId, pageable);
        List<WalletWithdrawInfo> walletDepositLogs = page.stream()
                .map(WalletWithdrawInfo::from)
                .toList();
        PageInfoDto pageInfo = new PageInfoDto(page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
        return new PagedResponseDto<>(walletDepositLogs, pageInfo);
    }

    // 예치금 충전 (내부 api로 수정 예정)
    @Transactional
    public ApiResponseDto<WalletInfo> chargeWallet(UUID memberId, WalletChargeCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for memberId: " + memberId));
        wallet.deposit(command.amount());

        WalletDepositLog walletDepositLog = WalletDepositLog.paidBuilder()
                .memberId(memberId)
                .paymentKey(command.paymentKey())
                .amount(command.amount())
                .build();
        walletDepositLogRepository.save(walletDepositLog);
        return new ApiResponseDto<>(HttpStatus.OK.value(), HttpStatus.OK.name(), WalletInfo.from(wallet));
    }

    // 예치금 정산 (내부 api로 수정 예정)
    @Transactional
    public ApiResponseDto<WalletInfo> settleWallet(UUID memberId, WalletSettleCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for memberId: " + memberId));

        wallet.deposit(command.amount());

        WalletDepositLog walletDepositLog = WalletDepositLog.settledBuilder()
                .memberId(memberId)
                .amount(command.amount())
                .build();
        walletDepositLogRepository.save(walletDepositLog);
        return new ApiResponseDto<>(HttpStatus.OK.value(), HttpStatus.OK.name(), WalletInfo.from(wallet));
    }

    //예치금 사용 (내부 api로 수정 예정)
    @Transactional
    public ApiResponseDto<WalletInfo> withdrawWallet(UUID memberId, WalletWithdrawCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for memberId: " + memberId));

        if (wallet.getBalance() < command.amount()) {
            throw new IllegalArgumentException("요청한 가격보다 예치금이 부족합니다.");
        }

        wallet.withdraw(command.amount());

        WalletWithdrawLog walletWithdrawLog = WalletWithdrawLog.builder()
                .memberId(memberId)
                .amount(command.amount())
                .build();
        walletWithdrawLogRepository.save(walletWithdrawLog);
        return new ApiResponseDto<>(HttpStatus.OK.value(), HttpStatus.OK.name(), WalletInfo.from(wallet));
    }

    // 예치금 환불 요청 (내부 api로 수정 예정)
    @Transactional
    public ApiResponseDto<WalletInfo> requestRefundWallet(UUID memberId, WalletRefundCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for memberId: " + memberId));

        WalletDepositLog walletDepositLog = walletDepositLogRepository.findById(command.walletDepositLogId())
                .orElseThrow(() -> new IllegalArgumentException("WalletDepositLog not found for id: " + command.walletDepositLogId()));

        if (walletDepositLog.getState() != PAID) {
            throw new IllegalArgumentException("환불 가능한 상태가 아닙니다.");
        }

        if (!walletDepositLog.getPaymentKey().equals(command.paymentKey())) {
            throw new IllegalArgumentException("결제 키가 일치하지 않습니다.");
        }

        if (wallet.getBalance() < walletDepositLog.getAmount()) {
            throw new IllegalArgumentException("요청한 가격보다 예치금이 부족합니다.");
        }

        wallet.withdraw(walletDepositLog.getAmount());
        walletDepositLog.changeState(CANCEL_WAITING);
        return new ApiResponseDto<>(HttpStatus.OK.value(), HttpStatus.OK.name(), WalletInfo.from(wallet));
    }

    // 예치금 환불 성공 (내부 api로 수정 예정)
    @Transactional
    public ApiResponseDto<WalletInfo> confirmRefundWallet(UUID memberId, WalletRefundCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for memberId: " + memberId));

        WalletDepositLog walletDepositLog = walletDepositLogRepository.findById(command.walletDepositLogId())
                .orElseThrow(() -> new IllegalArgumentException("WalletDepositLog not found for id: " + command.walletDepositLogId()));

        if (walletDepositLog.getState() != CANCEL_WAITING) {
            throw new IllegalArgumentException("환불 가능한 상태가 아닙니다.");
        }

        if (!walletDepositLog.getPaymentKey().equals(command.paymentKey())) {
            throw new IllegalArgumentException("결제 키가 일치하지 않습니다.");
        }

        walletDepositLog.changeState(CANCELED);
        return new ApiResponseDto<>(HttpStatus.OK.value(), HttpStatus.OK.name(), WalletInfo.from(wallet));
    }

    // 예치금 환불 실패 (내부 api로 수정 예정)
    @Transactional
    public ApiResponseDto<WalletInfo> failRefundWallet(UUID memberId, WalletRefundCommand command) {
        Wallet wallet = walletRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for memberId: " + memberId));

        WalletDepositLog walletDepositLog = walletDepositLogRepository.findById(command.walletDepositLogId())
                .orElseThrow(() -> new IllegalArgumentException("WalletDepositLog not found for id: " + command.walletDepositLogId()));

        if (walletDepositLog.getState() != CANCEL_WAITING) {
            throw new IllegalArgumentException("환불 가능한 상태가 아닙니다.");
        }

        if (!walletDepositLog.getPaymentKey().equals(command.paymentKey())) {
            throw new IllegalArgumentException("결제 키가 일치하지 않습니다.");
        }

        wallet.deposit(walletDepositLog.getAmount());
        walletDepositLog.changeState(PAID);
        return new ApiResponseDto<>(HttpStatus.OK.value(), HttpStatus.OK.name(), WalletInfo.from(wallet));
    }
}
