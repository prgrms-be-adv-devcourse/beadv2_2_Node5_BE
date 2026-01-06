package com.node5.memberservice.member.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shop-service")
public interface ShopClient {
    @GetMapping("/internal/shops")
    ResponseEntity<List<UUID>> getShopIds(@RequestParam UUID memberId);
}
