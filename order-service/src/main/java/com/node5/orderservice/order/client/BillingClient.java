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

@FeignClient(name = "billing-service")
public interface BillingClient {

    @PostMapping("/internal/wallets/withdraw")
    ResponseEntity<WalletInfo> withdraw(@Valid @RequestBody WalletWithdrawRequest request);

    @PutMapping("/internal/wallets/refund")
    ResponseEntity<WalletInfo> requestRefund(@Valid @RequestBody WalletRefundRequest request);
}