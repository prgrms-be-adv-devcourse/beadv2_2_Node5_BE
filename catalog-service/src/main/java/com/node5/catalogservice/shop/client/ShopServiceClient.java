package com.node5.catalogservice.shop.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.node5.catalogservice.shop.dto.ShopInfoResponse;

@FeignClient(name = "shop-service")
public interface ShopServiceClient {

	@GetMapping("/api/v1/shops/{shopId}")
	ShopInfoResponse getShopInfo(
		@RequestHeader("Member-Id") UUID memberId,
		@PathVariable("shopId") UUID shopId
	);
}
