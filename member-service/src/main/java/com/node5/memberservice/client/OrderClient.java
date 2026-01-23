package com.node5.memberservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderClient {
    @GetMapping("/internal/orders/in-progress")
    ResponseEntity<Boolean> hasInProgressOrder(@RequestHeader("Member-Id") UUID memberId);

    @PostMapping("/internal/orders/settlement-pending")
    ResponseEntity<Boolean> hasInProgressSettlementPending(@RequestBody List<UUID> productIds);
}
