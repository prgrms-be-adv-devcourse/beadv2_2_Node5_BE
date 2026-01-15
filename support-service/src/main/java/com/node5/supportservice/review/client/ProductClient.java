package com.node5.supportservice.review.client;

import com.node5.supportservice.review.client.dto.ProductStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "catalog-service", contextId = "reviewProductClient")
public interface ProductClient {

    @GetMapping("${api.v1}/products/{productId}/review-status")
    ProductStatusResponse canPostReview(@PathVariable UUID productId);

}
