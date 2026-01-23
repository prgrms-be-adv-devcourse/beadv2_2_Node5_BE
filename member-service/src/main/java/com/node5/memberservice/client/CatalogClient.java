package com.node5.memberservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "catalog-service")
public interface CatalogClient {
    @GetMapping("internal/products/getProductIds")
    ResponseEntity<List<UUID>> getProductIdsByShopIds(@RequestBody List<UUID> shopIds);
}
