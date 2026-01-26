package com.node5.paymentservice.payment.client.openfeign;

import com.node5.paymentservice.payment.client.openfeign.dto.WalletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

@FeignClient(name = "wallet-service", contextId = "walletClient")
public interface WalletClient {

    @GetMapping("/internal/wallets/existsByMemberId")
    RequestEntity<Boolean> existsByMemberId(UUID memberId);

    @PostMapping("/internal/wallets/depositRequest")
    ResponseEntity<Void> depositRequest(WalletRequest depositRequest);

    @PostMapping("/internal/wallets/withdrawRequest")
    ResponseEntity<Void> withdrawRequest(WalletRequest withdrawRequest);

    @PutMapping("/internal/wallets/cancelDepositRequest")
    ResponseEntity<Void> cancelDeposit(WalletRequest cancelDepositRequest);
}
