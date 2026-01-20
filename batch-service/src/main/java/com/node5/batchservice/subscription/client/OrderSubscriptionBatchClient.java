package com.node5.batchservice.subscription.client;

import com.node5.batchservice.subscription.client.dto.SubscriptionBatchTarget;
import com.node5.common.domain.PagedResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service")
public interface OrderSubscriptionBatchClient {

    @GetMapping("/internal/subscriptions/batch/targets")
    ResponseEntity<PagedResponseDto<SubscriptionBatchTarget>> findTargets(
            @RequestParam("runDate") String runDate,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );
}
