package com.node5.subscriptionservice.subscription.client;

import com.node5.subscriptionservice.subscription.client.dto.ShopInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "shop-service")
public interface ShopClient {

    @GetMapping("${api.v1}/shops/{shopId}")
    ResponseEntity<ShopInfoResponse> findMyShop(
            @RequestHeader("Member-Id") UUID memberId,
            @PathVariable UUID shopId
    );
}
