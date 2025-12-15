package com.node5.settlementservice.settlement.client;

import com.node5.settlementservice.settlement.client.dto.WalletSettleRequest;
import com.node5.settlementservice.settlement.client.dto.WalletInfo;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "billing-service")
public interface BillingClient {

    @PutMapping("/internal/wallets/settle")
    ResponseEntity<WalletInfo> settle(@Valid @RequestBody WalletSettleRequest request);

}
