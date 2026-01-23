package com.node5.supportservice.search.infrastructure.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.node5.supportservice.search.infrastructure.client.dto.ProductIdsRequest;
import com.node5.supportservice.search.infrastructure.client.dto.ProductIndexSummaryListResponse;

@FeignClient(name = "catalog-service", path = "/internal/products")
public interface CatalogInternalFeignClient {

	@GetMapping("/ids")
	List<UUID> getOnSaleProductIds(@RequestParam("page") int page, @RequestParam("size") int size);

	@PostMapping("/summaries")
	ProductIndexSummaryListResponse getProductSummaries(@RequestBody ProductIdsRequest request);
}
