package com.node5.catalogservice.product.presentation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.product.application.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("internal/products")
@RequiredArgsConstructor
public class ProductInternalController {

	private final ProductService productService;

	@PostMapping("/shop-ids")
	public ResponseEntity<Map<UUID, UUID>> getShopIdsByProductIds(@RequestBody List<UUID> productIds) {
		return ResponseEntity.ok(productService.getShopIdsByProductIds(productIds));
	}
}
