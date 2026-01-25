package com.node5.catalogservice.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "member-service")
public interface ShopOwnershipFeignClient {

	@GetMapping("/internal/shops/{shopId}/member-id")
	UUID getOwnerMemberId(@PathVariable("shopId") UUID shopId);
}
