package com.node5.batchservice.settlement.client;

import com.node5.batchservice.settlement.client.dto.WalletSettleRequest;
import com.node5.batchservice.settlement.client.dto.WalletSettleInfo;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "wallet-service")
public interface WalletClient {

    @PutMapping("/internal/wallets/settle")
    ResponseEntity<WalletSettleInfo> settle(
            @RequestHeader("Member-Id") UUID memberId,
            @Valid @RequestBody WalletSettleRequest request
    );

}
