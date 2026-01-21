package com.node5.batchservice.subscription.client;

import com.node5.batchservice.subscription.client.dto.OrderCreateInfo;
import com.node5.batchservice.subscription.client.dto.OrderCreateRequest;
import com.node5.batchservice.subscription.client.dto.SubscriptionBatchTarget;
import com.node5.common.domain.PagedResponseDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderClient {

    @PostMapping("${api.v1}/orders")
    ResponseEntity<OrderCreateInfo> create(
            @RequestHeader("Member-Id") UUID memberId,
            @RequestBody @Valid OrderCreateRequest request
    );

    @GetMapping("/internal/subscriptions/batch/targets")
    ResponseEntity<PagedResponseDto<SubscriptionBatchTarget>> findTargets(
            @RequestParam("runDate") String runDate,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );
}
