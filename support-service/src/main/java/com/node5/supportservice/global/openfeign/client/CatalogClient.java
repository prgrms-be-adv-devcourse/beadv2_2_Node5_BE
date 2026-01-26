package com.node5.supportservice.global.openfeign.client;

import com.node5.supportservice.global.openfeign.client.dto.ProductIdsRequest;
import com.node5.supportservice.global.openfeign.client.dto.ProductSummaryListResponse;
import com.node5.supportservice.global.openfeign.client.dto.ProductStatusResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "catalog-service", contextId = "catalogClient")
public interface CatalogClient {

    @PostMapping("/internal/products/getProductsByIds")
    ResponseEntity<ProductSummaryListResponse> getProductsByIds(
            @RequestHeader("Member-Id") UUID memberId,
            @Valid @RequestBody ProductIdsRequest request
    );

    @GetMapping("/internal/products/ids")
    ResponseEntity<List<UUID>> getProductIds(Pageable pageable);

    @GetMapping("/internal/products/{productId}/review-status")
    ProductStatusResponse canPostReview(@PathVariable UUID productId);
}
