package com.node5.supportservice.recommendation.client.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderClient {

    @GetMapping("/internal/orders/getRecentOrderList")
    ResponseEntity<List<UUID>> getRecentOrderList(
            @RequestHeader("Member-Id") UUID memberId
    );

}
