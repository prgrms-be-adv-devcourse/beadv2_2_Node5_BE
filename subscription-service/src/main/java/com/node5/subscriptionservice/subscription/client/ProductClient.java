package com.node5.subscriptionservice.subscription.client;

import com.node5.common.domain.ApiResponseDto;
import com.node5.subscriptionservice.subscription.client.dto.ProductInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "catalog-service")
public interface ProductClient {

    @GetMapping("${api.v1}/products/{productId}")
    ResponseEntity<ApiResponseDto<ProductInfoResponse>> findById(@PathVariable UUID productId);
}
