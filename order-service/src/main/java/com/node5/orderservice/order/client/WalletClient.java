package com.node5.orderservice.order.client;

import com.node5.orderservice.order.client.dto.WalletInfo;
import com.node5.orderservice.order.client.dto.WalletRefundRequest;
import com.node5.orderservice.order.client.dto.WalletWithdrawRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "wallet-service")
public interface WalletClient {

    @PostMapping("/internal/wallets/withdraw")
    ResponseEntity<WalletInfo> withdraw(
            @RequestHeader("Member-Id") UUID memberId,
            @Valid @RequestBody WalletWithdrawRequest request
    );

    @PutMapping("/internal/wallets/refund")
    ResponseEntity<WalletInfo> requestRefund(
            @RequestHeader("Member-Id") UUID memberId,
            @Valid @RequestBody WalletRefundRequest request
    );
}