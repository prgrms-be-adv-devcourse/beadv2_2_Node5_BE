package com.node5.orderservice.subscription.client;

import com.node5.orderservice.subscription.client.dto.ProductInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "catalog-service")
public interface ProductClient {

    @GetMapping("${api.v1}/products/{productId}")
    ResponseEntity<ProductInfoResponse> findById(@PathVariable UUID productId);
}
