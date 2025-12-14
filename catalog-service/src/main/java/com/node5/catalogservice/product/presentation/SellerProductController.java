package com.node5.catalogservice.product.presentation;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.product.application.ProductService;
import com.node5.catalogservice.product.application.dto.ProductInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.v1}/shops")
@RequiredArgsConstructor
@Tag(name = "Seller Products", description = "판매자 상점 상품 관리 API")
public class SellerProductController {

	private final ProductService productService;

	@GetMapping("/{shopId}/products")
	@Operation(
		summary = "내 상점 상품 목록 조회",
		description = "회원이 소유한 상점의 상품 목록을 상태와 관계없이 조회합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 목록 조회 성공"),
		@ApiResponse(responseCode = "403", description = "해당 상점에 대한 권한이 없습니다."),
		@ApiResponse(responseCode = "404", description = "상점을 찾을 수 없습니다.")
	})
	public ResponseEntity<Page<ProductInfo>> getMyShopProducts(
		@RequestHeader("Member-Id") UUID memberId,
		@PathVariable UUID shopId,
		@ParameterObject Pageable pageable
	) {
		return ResponseEntity.ok(
			productService.getProductsByShop(memberId, shopId, pageable)
		);
	}
}
