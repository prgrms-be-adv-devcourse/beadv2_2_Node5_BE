package com.node5.billingservice.wallet.presentation;

import com.node5.billingservice.wallet.application.WalletService;
import com.node5.billingservice.wallet.application.dto.WalletDepositInfo;
import com.node5.billingservice.wallet.application.dto.WalletInfo;
import com.node5.billingservice.wallet.application.dto.WalletWithdrawInfo;
import com.node5.billingservice.wallet.presentation.dto.WalletChargeRequest;
import com.node5.billingservice.wallet.presentation.dto.WalletRefundRequest;
import com.node5.billingservice.wallet.presentation.dto.WalletSettleRequest;
import com.node5.billingservice.wallet.presentation.dto.WalletWithdrawRequest;
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
    @GetMapping("/{memberId}")
    public ResponseEntity<WalletInfo> getWallet(@PathVariable UUID memberId) {
        return ResponseEntity.ok(walletService.getWallet(memberId));
    }

    @Operation(summary = "예치금 생성", description = "회원의 예치금 계좌를 생성한다.")
    @PostMapping("/{memberId}")
    public ResponseEntity<WalletInfo> createWallet(@PathVariable UUID memberId) {
        return ResponseEntity.status(CREATED).body(walletService.createWallet(memberId));
    }

    @Operation(summary = "예치금 입금 내역 조회", description = "회원의 예치금 입금 내역을 조회한다.")
    @GetMapping("/{memberId}/deposits")
    public ResponseEntity<Page<WalletDepositInfo>> getDepositLogs(@PathVariable UUID memberId, Pageable pageable) {
        return ResponseEntity.ok(walletService.getDeposits(memberId, pageable));
    }

    @Operation(summary = "예치금 출금 내역 조회", description = "회원의 예치금 출금 내역을 조회한다.")
    @GetMapping("/{memberId}/withdraws")
    public ResponseEntity<Page<WalletWithdrawInfo>> getWithdraws(@PathVariable UUID memberId, Pageable pageable) {
        return ResponseEntity.ok(walletService.getWithdraws(memberId, pageable));
    }

    @Operation(summary = "예치금 충전", description = "회원의 예치금을 충전한다.")
    @PutMapping("/{memberId}/charge")
    public ResponseEntity<WalletInfo> charge(@PathVariable UUID memberId, @Valid @RequestBody WalletChargeRequest request) {
        return ResponseEntity.ok(walletService.chargeWallet(memberId, request.toCommand()));
    }

    @Operation(summary = "예치금 정산", description = "회원의 예치금을 정산받는다.")
    @PutMapping("/{memberId}/settle")
    public ResponseEntity<WalletInfo> settle(@PathVariable UUID memberId, @Valid @RequestBody WalletSettleRequest request) {
        return ResponseEntity.ok(walletService.settleWallet(memberId, request.toCommand()));
    }

    @Operation(summary = "예치금 사용", description = "회원의 예치금을 상품 주문에 사용한다.")
    @PutMapping("/{memberId}/withdraw")
    public ResponseEntity<WalletInfo> withdraw(@PathVariable UUID memberId, @Valid @RequestBody WalletWithdrawRequest request) {
        return ResponseEntity.ok(walletService.withdrawWallet(memberId, request.toCommand()));
    }

    @Operation(summary = "예치금 환불 요청", description = "회원의 예치금을 환불 요청한다.")
    @PutMapping("/{memberId}/refund")
    public ResponseEntity<WalletInfo> requestRefund(@PathVariable UUID memberId, @Valid @RequestBody WalletRefundRequest request) {
        return ResponseEntity.ok(walletService.requestRefundWallet(memberId, request.toCommand()));
    }

    @Operation(summary = "예치금 환불 성공", description = "회원의 예치금 환불이 성공한다.")
    @PutMapping("/{memberId}/refund/success")
    public ResponseEntity<WalletInfo> successRefund(@PathVariable UUID memberId, @Valid @RequestBody WalletRefundRequest request) {
        return ResponseEntity.ok(walletService.confirmRefundWallet(memberId, request.toCommand()));
    }

    @Operation(summary = "예치금 환불 실패", description = "회원의 예치금 환불이 실패한다.")
    @PutMapping("/{memberId}/refund/fail")
    public ResponseEntity<WalletInfo> failRefund(@PathVariable UUID memberId, @Valid @RequestBody WalletRefundRequest request) {
        return ResponseEntity.ok(walletService.failRefundWallet(memberId, request.toCommand()));
    }

}
