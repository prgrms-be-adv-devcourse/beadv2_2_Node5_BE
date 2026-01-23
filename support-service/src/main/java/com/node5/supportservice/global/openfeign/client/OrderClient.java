package com.node5.supportservice.global.openfeign.client;

import com.node5.supportservice.global.openfeign.client.dto.OrderStatusRequest;
import com.node5.supportservice.global.openfeign.client.dto.OrderStatusResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order-service", contextId = "orderClient")
public interface OrderClient {

    @GetMapping("/internal/orders/getRecentOrderList")
    ResponseEntity<List<UUID>> getRecentOrderList(
            @RequestHeader("Member-Id") UUID memberId
    );

    @PostMapping("/internal/orders/review-status")
    ResponseEntity<Boolean> canPostReview(
            @RequestHeader("Member-Id") UUID memberId,
            @Valid @RequestBody OrderStatusRequest request
    );

}
