package com.node5.walletservice.wallet.presentation;

import com.node5.common.domain.ApiResponseDto;
import com.node5.common.domain.PagedResponseDto;
import com.node5.walletservice.wallet.application.WalletService;
import com.node5.walletservice.wallet.application.dto.WalletDepositInfo;
import com.node5.walletservice.wallet.application.dto.WalletInfo;
import com.node5.walletservice.wallet.application.dto.WalletWithdrawInfo;
import com.node5.walletservice.wallet.presentation.dto.WalletChargeRequest;
import com.node5.walletservice.wallet.presentation.dto.WalletRefundRequest;
import com.node5.walletservice.wallet.presentation.dto.WalletSettleRequest;
import com.node5.walletservice.wallet.presentation.dto.WalletWithdrawRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${api.v1}/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @Operation(summary = "예치금 조회", description = "회원의 예치금 정보를 조회한다.")
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponseDto<WalletInfo>> getWallet(@PathVariable UUID memberId) {
        return walletService.getWallet(memberId);
    }

    @Operation(summary = "예치금 생성", description = "회원의 예치금 계좌를 생성한다.")
    @PostMapping("/{memberId}")
    public ResponseEntity<ApiResponseDto<WalletInfo>> createWallet(@PathVariable UUID memberId) {
        return  walletService.createWallet(memberId);
    }

    @Operation(summary = "예치금 입금 내역 조회", description = "회원의 예치금 입금 내역을 조회한다.")
    @GetMapping("/{memberId}/deposits")
    public ResponseEntity<ApiResponseDto<PagedResponseDto<WalletDepositInfo>>> getDepositLogs(@PathVariable UUID memberId, Pageable pageable) {
        return walletService.getDeposits(memberId, pageable);
    }

    @Operation(summary = "예치금 출금 내역 조회", description = "회원의 예치금 출금 내역을 조회한다.")
    @GetMapping("/{memberId}/withdraws")
    public ResponseEntity<ApiResponseDto<PagedResponseDto<WalletWithdrawInfo>>> getWithdraws(@PathVariable UUID memberId, Pageable pageable) {
        return walletService.getWithdraws(memberId, pageable);
    }

    @Operation(summary = "예치금 충전", description = "회원의 예치금을 충전한다.")
    @PutMapping("/{memberId}/charge")
    public ResponseEntity<ApiResponseDto<WalletInfo>> charge(@PathVariable UUID memberId, @RequestBody WalletChargeRequest request) {
        return walletService.chargeWallet(memberId, request.toCommand());
    }

    @Operation(summary = "예치금 정산", description = "회원의 예치금을 정산받는다.")
    @PutMapping("/{memberId}/settle")
    public ResponseEntity<ApiResponseDto<WalletInfo>> settle(@PathVariable UUID memberId, @RequestBody WalletSettleRequest request) {
        return walletService.settleWallet(memberId, request.toCommand());
    }

    @Operation(summary = "예치금 사용", description = "회원의 예치금을 상품 주문에 사용한다.")
    @PutMapping("/{memberId}/withdraw")
    public ResponseEntity<ApiResponseDto<WalletInfo>> withdraw(@PathVariable UUID memberId, @RequestBody WalletWithdrawRequest request) {
        return walletService.withdrawWallet(memberId, request.toCommand());
    }

    @Operation(summary = "예치금 환불 요청", description = "회원의 예치금을 환불 요청한다.")
    @PutMapping("/{memberId}/refund")
    public ResponseEntity<ApiResponseDto<WalletInfo>> requestRefund(@PathVariable UUID memberId, @RequestBody WalletRefundRequest request) {
        return walletService.requestRefundWallet(memberId, request.toCommand());
    }

    @Operation(summary = "예치금 환불 성공", description = "회원의 예치금 환불이 성공한다.")
    @PutMapping("/{memberId}/refund/success")
    public ResponseEntity<ApiResponseDto<WalletInfo>> successRefund(@PathVariable UUID memberId, @RequestBody WalletRefundRequest request) {
        return walletService.confirmRefundWallet(memberId, request.toCommand());
    }

    @Operation(summary = "예치금 환불 실패", description = "회원의 예치금 환불이 실패한다.")
    @PutMapping("/{memberId}/refund/fail")
    public ResponseEntity<ApiResponseDto<WalletInfo>> failRefund(@PathVariable UUID memberId, @RequestBody WalletRefundRequest request) {
        return walletService.failRefundWallet(memberId, request.toCommand());
    }

}
