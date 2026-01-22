package com.node5.memberservice.settlement.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderClient {

    @PostMapping("/internal/orders/settlement-pending")
    ResponseEntity<Boolean> hasInProgressSettlementPending(@RequestBody List<UUID> productIds);

}