package com.node5.settlementservice.settlement.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "member-service")
public interface MemberClient {

    @GetMapping("/internal/shops/{shopId}/member-id")
    ResponseEntity<UUID> getMemberIdByShopId(@PathVariable UUID shopId);

}
