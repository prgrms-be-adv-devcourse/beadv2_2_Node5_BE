package com.node5.catalogservice.shop.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "shop-service")
public interface ShopOwnershipClient {

	@GetMapping("/internal/shops/{shopId}/member-id")
	UUID getOwnerMemberId(@PathVariable("shopId") UUID shopId);
}
