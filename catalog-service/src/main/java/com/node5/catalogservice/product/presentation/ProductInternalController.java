package com.node5.catalogservice.product.presentation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.product.application.ProductInternalService;
import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.presentation.dto.ProductIdsRequest;
import com.node5.catalogservice.product.presentation.dto.ProductReviewStatusResponse;
import com.node5.catalogservice.product.presentation.dto.ProductSummaryListResponse;
import com.node5.catalogservice.product.presentation.dto.ProductSummaryResponse;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/products")
@RequiredArgsConstructor
@Hidden
public class ProductInternalController {

	private final ProductInternalService productInternalService;

	@PostMapping("/shop-ids")
	public ResponseEntity<Map<UUID, UUID>> getShopIdsByProductIds(@RequestBody List<UUID> productIds) {
		return ResponseEntity.ok(productInternalService.getShopIdsByProductIds(productIds));
	}

	@PostMapping("/getProductsByIds")
	public ResponseEntity<ProductSummaryListResponse> getProductsByIds(
		@RequestHeader("Member-Id") UUID memberId,
		@Valid @RequestBody ProductIdsRequest request
	) {
		List<UUID> ids = request.productIds();
		List<Product> products = productInternalService.getProductsByIds(ids);

		Map<UUID, Integer> order = new java.util.HashMap<>();
		for (int i = 0; i < ids.size(); i++) order.put(ids.get(i), i);

		List<ProductSummaryResponse> summaries = products.stream()
			.map(p -> new ProductSummaryResponse(
				p.getId(),
				p.getName(),
				p.getCategory().name(),
				p.getDescription()
			))
			.sorted(java.util.Comparator.comparingInt(s -> order.getOrDefault(s.productId(), Integer.MAX_VALUE)))
			.toList();

		return ResponseEntity.ok(new ProductSummaryListResponse(summaries));
	}

	@GetMapping("/ids")
	public ResponseEntity<List<UUID>> getProductIds(@ParameterObject Pageable pageable) {
		return ResponseEntity.ok(productInternalService.getOnSaleProductIds(pageable));
	}

	@GetMapping("/getProductIds")
	public ResponseEntity<List<UUID>> getProductIdsByShopIds(@RequestBody List<UUID> shopIds) {
		return ResponseEntity.ok(productInternalService.getProductIdsByShopIds(shopIds));
	}

	@GetMapping("/{productId}/review-status")
	public ResponseEntity<ProductReviewStatusResponse> canPostReview(@PathVariable UUID productId) {
		boolean reviewable = productInternalService.isReviewable(productId);
		return ResponseEntity.ok(new ProductReviewStatusResponse(reviewable));
	}
}
