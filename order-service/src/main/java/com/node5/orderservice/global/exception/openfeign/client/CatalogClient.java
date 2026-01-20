package com.node5.orderservice.global.exception.openfeign.client;

import com.node5.orderservice.global.exception.openfeign.client.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "catalog-service")
public interface CatalogClient {

    // productId <-> shopId 매핑
    @PostMapping("/api/v1/products/shop-ids")
    ResponseEntity<Map<UUID, UUID>> getShopIdsByProductIds(@RequestBody List<UUID> productIds);

    // 재고 선점
    @PostMapping("/internal/stocks/reservations/hold")
    ResponseEntity<StockHoldBatchResult> hold(@RequestBody StockHoldBatchRequest request);

    // 재고 확정
    @PostMapping("/internal/stocks/reservations/commit")
    ResponseEntity<Void> commit(@RequestBody StockCommitBatchRequest request);

    // 재고 해제
    @PostMapping("/internal/stocks/reservations/release")
    ResponseEntity<Void> release(@RequestBody StockReleaseBatchRequest request);

    @GetMapping("${api.v1}/products/{productId}")
    ResponseEntity<ProductInfoResponse> findById(@PathVariable UUID productId);
}
