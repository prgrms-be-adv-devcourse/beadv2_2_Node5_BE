package com.node5.billingservice.wallet.presentation;

import com.node5.billingservice.wallet.application.WalletService;
import com.node5.billingservice.wallet.application.dto.WalletInfo;
import com.node5.billingservice.wallet.presentation.dto.WalletRefundRequest;
import com.node5.billingservice.wallet.presentation.dto.WalletSettleRequest;
import com.node5.billingservice.wallet.presentation.dto.WalletWithdrawRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/internal/wallets")
@RequiredArgsConstructor
public class WalletInternalController {

    private final WalletService walletService;

    @Operation(summary = "예치금 생성", description = "회원의 예치금 계좌를 생성한다.")
    @PostMapping
    public ResponseEntity<WalletInfo> createWallet(@RequestHeader("Member-Id") UUID memberId) {
        return ResponseEntity.status(CREATED).body(walletService.createWallet(memberId));
    }

//    @Operation(summary = "예치금 충전", description = "회원의 예치금을 충전한다.")
//    @PutMapping("/{memberId}/charge")
//    public ResponseEntity<WalletInfo> charge(@PathVariable UUID memberId, @Valid @RequestBody WalletChargeRequest request) {
//        return ResponseEntity.ok(walletService.chargeWallet(memberId, request.toCommand()));
//    }

    @Operation(summary = "예치금 정산", description = "회원의 예치금을 정산받는다.")
    @PutMapping("/settle")
    public ResponseEntity<WalletInfo> settle(@RequestHeader("Member-Id") UUID memberId, @Valid @RequestBody WalletSettleRequest request) {
        return ResponseEntity.ok(walletService.settleWallet(memberId, request.toCommand()));
    }

    @Operation(summary = "예치금 사용", description = "회원의 예치금을 상품 주문에 사용한다.")
    @PostMapping("/withdraw")
    public ResponseEntity<WalletInfo> withdraw(@RequestHeader("Member-Id") UUID memberId, @Valid @RequestBody WalletWithdrawRequest request) {
        return ResponseEntity.ok(walletService.withdrawWallet(memberId, request.toCommand()));
    }

    @Operation(summary = "예치금 환불", description = "사용자가 주문을 취소하면 회원의 예치금 사용 기록을 확인 후 예치금을 환불받는다.")
    @PutMapping("/refund")
    public ResponseEntity<WalletInfo> requestRefund(@RequestHeader("Member-Id") UUID memberId, @Valid @RequestBody WalletRefundRequest request) {
        return ResponseEntity.ok(walletService.refundWallet(memberId, request.toCommand()));
    }

//    @Operation(summary = "예치금 환불 성공", description = "회원의 예치금 환불이 성공한다.")
//    @PutMapping("/{memberId}/refund/success")
//    public ResponseEntity<WalletInfo> successRefund(@PathVariable UUID memberId, @Valid @RequestBody WalletRefundRequest request) {
//        return ResponseEntity.ok(walletService.confirmRefundWallet(memberId, request.toCommand()));
//    }
//
//    @Operation(summary = "예치금 환불 실패", description = "회원의 예치금 환불이 실패한다.")
//    @PutMapping("/{memberId}/refund/fail")
//    public ResponseEntity<WalletInfo> failRefund(@PathVariable UUID memberId, @Valid @RequestBody WalletRefundRequest request) {
//        return ResponseEntity.ok(walletService.failRefundWallet(memberId, request.toCommand()));
//    }
}
