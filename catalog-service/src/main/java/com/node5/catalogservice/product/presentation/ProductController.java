package com.node5.catalogservice.product.presentation;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.product.application.ProductService;
import com.node5.catalogservice.product.application.dto.ProductInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.v1}/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "사용자가 조회 가능한 상품 정보 조회 API")
public class ProductController {

	private final ProductService productService;

	@GetMapping
	@Operation(
		summary = "판매 중인 상품 목록 조회",
		description = "판매 중(ON_SALE) 상품 목록을 페이징으로 조회합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "판매 중 상품 목록 조회 성공")
	})
	public ResponseEntity<Page<ProductInfo>> getProducts(
		@ParameterObject Pageable pageable
	) {
		return ResponseEntity.ok(productService.getOnSaleProducts(pageable));
	}

	@GetMapping("/{productId}")
	@Operation(
		summary = "판매 중 상품 상세 조회",
		description = "상품 ID로 판매 중(ON_SALE) 상품을 조회합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "판매 중 상품 조회 성공"),
		@ApiResponse(responseCode = "404", description = "해당 ID의 판매 중인 상품이 없습니다.")
	})
	public ResponseEntity<ProductInfo> getProduct(
		@Parameter(description = "상품 ID") @PathVariable UUID productId
	) {
		return ResponseEntity.ok(productService.getOnSaleProduct(productId));
	}
}
