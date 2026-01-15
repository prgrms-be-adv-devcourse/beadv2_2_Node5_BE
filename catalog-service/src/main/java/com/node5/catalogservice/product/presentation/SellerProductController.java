package com.node5.catalogservice.product.presentation;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.product.application.ProductService;
import com.node5.catalogservice.product.application.dto.ProductCommand;
import com.node5.catalogservice.product.application.dto.ProductInfo;
import com.node5.catalogservice.product.application.dto.ProductUpdateCommand;
import com.node5.catalogservice.product.presentation.dto.ProductRequest;
import com.node5.catalogservice.product.presentation.dto.ProductStatusUpdateRequest;
import com.node5.catalogservice.product.presentation.dto.ProductUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.v1}/seller")
@RequiredArgsConstructor
@Tag(name = "Seller Products", description = "판매자 상품 관리 API")
public class SellerProductController {

	private final ProductService productService;

	@GetMapping("/shops/{shopId}/products")
	@Operation(
		summary = "내 상점 상품 목록 조회",
		description = "회원이 소유한 상점(shopId)의 상품 목록을 상태와 관계없이 페이징 조회합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 목록 조회 성공"),
		@ApiResponse(responseCode = "403", description = "해당 상점에 대한 권한이 없습니다."),
		@ApiResponse(responseCode = "404", description = "상점을 찾을 수 없습니다."),
		@ApiResponse(responseCode = "503", description = "상점 서비스 장애로 조회할 수 없습니다.")
	})
	public ResponseEntity<Page<ProductInfo>> getMyShopProducts(
		@RequestHeader("Member-Id") UUID memberId,
		@PathVariable("shopId") UUID shopId,
		@ParameterObject Pageable pageable
	) {
		return ResponseEntity.ok(productService.getProductsByShop(memberId, shopId, pageable));
	}

	@PostMapping("/shops/{shopId}/products")
	@Operation(
		summary = "상품 생성",
		description = "회원이 소유한 상점(shopId)에 새로운 상품을 등록합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "상품 생성 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다."),
		@ApiResponse(responseCode = "403", description = "해당 상점에 대한 권한이 없습니다."),
		@ApiResponse(responseCode = "404", description = "상점을 찾을 수 없습니다."),
		@ApiResponse(responseCode = "503", description = "상점 서비스 장애로 조회할 수 없습니다.")
	})
	public ResponseEntity<ProductInfo> createProduct(
		@RequestHeader("Member-Id") UUID memberId,
		@PathVariable("shopId") UUID shopId,
		@Valid @RequestBody ProductRequest request
	) {
		ProductCommand command = request.toCommand();
		ProductInfo created = productService.createProduct(memberId, shopId, command);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/products/{productId}")
	@Operation(
		summary = "상품 수정",
		description = "상품 기본 정보를 수정합니다. 요청자는 해당 상품이 속한 상점의 소유자여야 합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 수정 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다."),
		@ApiResponse(responseCode = "403", description = "해당 상품/상점에 대한 권한이 없습니다."),
		@ApiResponse(responseCode = "404", description = "상품 또는 상점을 찾을 수 없습니다."),
		@ApiResponse(responseCode = "409", description = "판매 중단된 상품은 수정/상태 변경이 불가합니다."),
		@ApiResponse(responseCode = "503", description = "상점 서비스 장애로 조회할 수 없습니다.")
	})
	public ResponseEntity<ProductInfo> updateProduct(
		@RequestHeader("Member-Id") UUID memberId,
		@PathVariable("productId") UUID productId,
		@Valid @RequestBody ProductUpdateRequest request
	) {
		ProductUpdateCommand command = request.toCommand();
		return ResponseEntity.ok(productService.updateProduct(memberId, productId, command));
	}

	@PatchMapping("/products/{productId}/status")
	@Operation(
		summary = "상품 상태 변경",
		description = "상품 상태를 변경합니다. 요청자는 해당 상품이 속한 상점의 소유자여야 합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 상태 변경 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다."),
		@ApiResponse(responseCode = "403", description = "해당 상품/상점에 대한 권한이 없습니다."),
		@ApiResponse(responseCode = "404", description = "상품 또는 상점을 찾을 수 없습니다."),
		@ApiResponse(responseCode = "409", description = "판매 중단된 상품은 수정/상태 변경이 불가합니다."),
		@ApiResponse(responseCode = "503", description = "상점 서비스 장애로 조회할 수 없습니다.")
	})
	public ResponseEntity<ProductInfo> updateProductStatus(
		@RequestHeader("Member-Id") UUID memberId,
		@PathVariable("productId") UUID productId,
		@Valid @RequestBody ProductStatusUpdateRequest request
	) {
		return ResponseEntity.ok(productService.updateStatus(memberId, productId, request.status()));
	}

	@DeleteMapping("/products/{productId}")
	@Operation(
		summary = "상품 판매 중단",
		description = "상품을 판매 중단(DISCONTINUED) 처리합니다. 요청자는 해당 상품이 속한 상점의 소유자여야 합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "상품 판매 중단 성공"),
		@ApiResponse(responseCode = "403", description = "해당 상품/상점에 대한 권한이 없습니다."),
		@ApiResponse(responseCode = "404", description = "상품 또는 상점을 찾을 수 없습니다."),
		@ApiResponse(responseCode = "503", description = "상점 서비스 장애로 조회할 수 없습니다.")
	})
	public ResponseEntity<Void> discontinueProduct(
		@RequestHeader("Member-Id") UUID memberId,
		@PathVariable("productId") UUID productId
	) {
		productService.discontinueProduct(memberId, productId);
		return ResponseEntity.noContent().build();
	}
}
