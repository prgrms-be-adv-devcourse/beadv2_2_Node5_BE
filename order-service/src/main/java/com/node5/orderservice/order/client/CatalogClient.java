package com.node5.orderservice.order.client;

import com.node5.orderservice.order.client.dto.StockCommitBatchRequest;
import com.node5.orderservice.order.client.dto.StockHoldBatchRequest;
import com.node5.orderservice.order.client.dto.StockHoldBatchResult;
import com.node5.orderservice.order.client.dto.StockReleaseBatchRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "catalog-service")
public interface CatalogClient {

    // productId <-> shopId 매핑
    @PostMapping("/internal/products/shop-ids")
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

}
