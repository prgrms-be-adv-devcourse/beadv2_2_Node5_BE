package com.node5.subscriptionservice.subscription.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "shop-service")
public interface ShopClient {

    @GetMapping("internal/shops/{shopId}/member-id")
    ResponseEntity<UUID> getMemberIdByShopId(@PathVariable UUID shopId);
}
