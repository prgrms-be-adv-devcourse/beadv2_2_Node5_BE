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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.product.application.ProductService;
import com.node5.catalogservice.product.application.dto.ProductInfo;
import com.node5.catalogservice.product.domain.ProductStatus;
import com.node5.catalogservice.product.presentation.dto.ProductRequest;
import com.node5.catalogservice.product.presentation.dto.StatusRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.v1}/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "상품 정보 관리 API")
public class ProductController {

	private final ProductService productService;

	@GetMapping
	@Operation(summary = "판매 중인 상품 목록 조회", description = "판매 중인 상품 목록을 페이징으로 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "판매 중인 상품 목록 조회 성공")
	})
	public ResponseEntity<Page<ProductInfo>> list(@ParameterObject Pageable pageable) {
		Page<ProductInfo> result = productService.getOnSaleProducts(pageable);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/{id}")
	@Operation(summary = "판매 중 상품 상세 조회", description = "상품 ID로 판매 중인 상품을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "판매 중 상품 조회 성공"),
		@ApiResponse(responseCode = "404", description = "해당 ID의 판매 중인 상품이 없습니다.")
	})
	public ResponseEntity<ProductInfo> get(@Parameter(description = "상품 ID") @PathVariable UUID id) {
		ProductInfo result = productService.getOnSaleProduct(id);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/all")
	@Operation(summary = "전체 상품 목록 조회", description = "상품 상태와 관계 없이 전체 상품을 페이징으로 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "전체 상품 목록 조회 성공")
	})
	public ResponseEntity<Page<ProductInfo>> listAll(@ParameterObject Pageable pageable) {
		Page<ProductInfo> result = productService.getProducts(pageable);
		return ResponseEntity.ok(result);
	}

	@PostMapping
	@Operation(summary = "상품 등록", description = "판매할 상품을 신규로 등록합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "상품 등록 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다.")
	})
	public ResponseEntity<ProductInfo> create(@RequestBody ProductRequest request) {
		ProductInfo result = productService.createProduct(request.toCreateCommand());
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@PatchMapping("/{id}")
	@Operation(summary = "상품 정보 수정", description = "상품 ID로 기존 상품 정보를 수정합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 정보 수정 성공"),
		@ApiResponse(responseCode = "404", description = "해당 ID의 상품이 없습니다.")
	})
	public ResponseEntity<ProductInfo> update(
		@Parameter(description = "상품 ID") @PathVariable UUID id,
		@RequestBody ProductRequest request
	) {
		ProductInfo result = productService.updateProduct(id, request.toUpdateCommand());
		return ResponseEntity.ok(result);
	}

	@PatchMapping("/{id}/status")
	@Operation(summary = "상품 상태 변경", description = "상품 상태를 판매 중/일시 중단 등으로 변경합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 상태 변경 성공"),
		@ApiResponse(responseCode = "404", description = "해당 ID의 상품이 없습니다.")
	})
	public ResponseEntity<ProductInfo> updateStatus(
		@Parameter(description = "상품 ID") @PathVariable UUID id,
		@RequestBody StatusRequest request
	) {
		ProductStatus status = request.status();
		ProductInfo result = productService.updateStatus(id, status);
		return ResponseEntity.ok(result);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "상품 판매 중단", description = "상품을 판매 목록에서 제외하고 더 이상 노출되지 않도록 합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "상품 판매 중단 성공"),
		@ApiResponse(responseCode = "404", description = "해당 ID의 상품이 없습니다.")
	})
	public ResponseEntity<Void> discontinue(@Parameter(description = "상품 ID") @PathVariable UUID id) {
		productService.discontinueProduct(id);
		return ResponseEntity.noContent().build();
	}
}
