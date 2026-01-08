package com.node5.billingservice.wallet.presentation;

import com.node5.billingservice.wallet.application.WalletService;
import com.node5.billingservice.wallet.application.dto.*;
import com.node5.billingservice.wallet.presentation.dto.WalletTransferRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("${api.v1}/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @Operation(summary = "예치금 조회", description = "회원의 예치금 정보를 조회한다.")
    @GetMapping
    public ResponseEntity<WalletInfo> getWallet(@RequestHeader("Member-Id") UUID memberId) {
        return ResponseEntity.ok(walletService.getWallet(memberId));
    }

    @Operation(summary  = "예치금 생성", description = "회원의 예치금 계좌를 생성한다.")
    @PostMapping
    public ResponseEntity<WalletInfo> createWallet(@RequestHeader("Member-Id") UUID memberId) {
        return ResponseEntity.status(CREATED).body(walletService.createWallet(memberId));
    }

    @Operation(summary = "예치금 거래 내역 조회", description = "회원의 예치금 거래 내역을 조회한다.")
    @GetMapping("/transactions/all")
    public ResponseEntity<Page<WalletLogInfo>> getTransactionLogs(@RequestHeader("Member-Id") UUID memberId, Pageable pageable) {
        return ResponseEntity.ok(walletService.getTransactions(memberId, pageable));
    }

    @Operation(summary = "예치금 입금 내역 조회", description = "회원의 예치금 입금 내역을 조회한다.")
    @GetMapping("/transactions/deposits")
    public ResponseEntity<Page<WalletLogInfo>> getDepositLogs(@RequestHeader("Member-Id") UUID memberId, Pageable pageable) {
        return ResponseEntity.ok(walletService.getDeposits(memberId, pageable));
    }

    @Operation(summary = "예치금 출금 내역 조회", description = "회원의 예치금 출금 내역을 조회한다.")
    @GetMapping("/transactions/withdraws")
    public ResponseEntity<Page<WalletLogInfo>> getWithdraws(@RequestHeader("Member-Id") UUID memberId, Pageable pageable) {
        return ResponseEntity.ok(walletService.getWithdraws(memberId, pageable));
    }

    @Operation(summary = "예치금 이체", description = "회원의 예치금을 이체한다.")
    @PostMapping("/transfer")
    public ResponseEntity<WalletTransferInfo>transferWallet(@RequestHeader("Member-Id") UUID memberId, @Valid @RequestBody WalletTransferRequest request) {
        return ResponseEntity.ok(walletService.transferWallet(memberId, request.toCommand()));
    }

}
