package com.node5.supportservice.reviewsummary.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "catalog-service", contextId = "reviewsummaryCatalogClient")
public interface CatalogClient {

    @GetMapping("/internal/products/ids")
    ResponseEntity<List<UUID>> getProductIds(Pageable pageable);

}
