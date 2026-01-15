package com.node5.supportservice.recommendation.client.openfeign;

import com.node5.supportservice.recommendation.client.dto.ProductIdsRequest;
import com.node5.supportservice.recommendation.client.dto.ProductSummaryListResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "catalog-service", contextId = "recommendationCatalogClient")
public interface CatalogClient {

    @GetMapping("/internal/products/getProductsByIds")
    ResponseEntity<ProductSummaryListResponse> getProductsByIds(
            @RequestHeader("Member-Id") UUID memberId,
            @Valid @RequestBody ProductIdsRequest request
    );

}
