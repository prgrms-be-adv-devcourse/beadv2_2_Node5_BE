package com.node5.subscriptionservice.subscription.client;

import com.node5.subscriptionservice.subscription.client.dto.OrderCreateInfo;
import com.node5.subscriptionservice.subscription.client.dto.OrderCreateRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service")
public interface OrderClient {

    @PostMapping("${api.v1}/orders")
    ResponseEntity<OrderCreateInfo> create(@RequestBody @Valid OrderCreateRequest request);
}
