package com.node5.supportservice.review.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "catalog-service", contextId = "reviewProductClient")
public interface ProductClient {

    @GetMapping("/${api.v1}/products/{productId}/review-status")
    boolean canPostReview(@PathVariable UUID productId);

}
