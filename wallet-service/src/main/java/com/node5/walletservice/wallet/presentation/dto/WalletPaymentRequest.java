package com.node5.walletservice.wallet.presentation.dto;

import com.node5.walletservice.wallet.application.dto.WalletPaymentCommand;

import java.util.UUID;

public record WalletPaymentRequest(
        UUID memberId,   // 회원 식별자
        String orderId,  // 주문 번호 (상태 추적 및 멱등성 보장용)
        Long amount      // 요청 금액
) {
    public WalletPaymentCommand toCommand() {
        return new WalletPaymentCommand(memberId, orderId, amount);
    }
}