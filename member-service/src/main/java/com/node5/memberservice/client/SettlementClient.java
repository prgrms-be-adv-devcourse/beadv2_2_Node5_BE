package com.node5.memberservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "settlement-service")
public interface SettlementClient {
    @PostMapping("/internal/settlements/in-progress")
    ResponseEntity<Boolean> hasInProgressSettlement(@RequestBody List<UUID> shopIdList);
}
