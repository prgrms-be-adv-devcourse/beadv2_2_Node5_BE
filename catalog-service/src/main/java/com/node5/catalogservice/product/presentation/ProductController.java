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

import com.node5.catalogservice.product.application.ProductImageService;
import com.node5.catalogservice.product.application.ProductService;
import com.node5.catalogservice.product.application.dto.PresignedUrlInfo;
import com.node5.catalogservice.product.application.dto.ProductCommand;
import com.node5.catalogservice.product.application.dto.ProductInfo;
import com.node5.catalogservice.product.application.dto.ProductUpdateCommand;
import com.node5.catalogservice.product.domain.ProductStatus;
import com.node5.catalogservice.product.presentation.dto.PresignedUrlRequest;
import com.node5.catalogservice.product.presentation.dto.PresignedUrlResponse;
import com.node5.catalogservice.product.presentation.dto.ProductRequest;
import com.node5.catalogservice.product.presentation.dto.StatusRequest;
import com.node5.common.domain.ApiResponseDto;
import com.node5.common.domain.PageInfoDto;
import com.node5.common.domain.PagedResponseDto;

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
	private final ProductImageService productImageService;

	@GetMapping
	@Operation(summary = "판매 중인 상품 목록 조회", description = "판매 중인 상품 목록을 페이징으로 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "판매 중인 상품 목록 조회 성공")
	})
	public ResponseEntity<ApiResponseDto<PagedResponseDto<ProductInfo>>> getProducts(
		@ParameterObject Pageable pageable
	) {
		Page<ProductInfo> page = productService.getOnSaleProducts(pageable);

		PagedResponseDto<ProductInfo> paged = new PagedResponseDto<>(
			page.getContent(),
			new PageInfoDto(
				page.getNumber(),
				page.getSize(),
				page.getTotalElements(),
				page.getTotalPages()
			)
		);

		ApiResponseDto<PagedResponseDto<ProductInfo>> response =
			new ApiResponseDto<>(HttpStatus.OK.value(), "판매 중인 상품 목록 조회 성공", paged);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	@Operation(summary = "판매 중 상품 상세 조회", description = "상품 ID로 판매 중인 상품을 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "판매 중 상품 조회 성공"),
		@ApiResponse(responseCode = "404", description = "해당 ID의 판매 중인 상품이 없습니다.")
	})
	public ResponseEntity<ApiResponseDto<ProductInfo>> getProduct(
		@Parameter(description = "상품 ID") @PathVariable UUID id
	) {
		ProductInfo result = productService.getOnSaleProduct(id);

		ApiResponseDto<ProductInfo> response =
			new ApiResponseDto<>(HttpStatus.OK.value(), "판매 중 상품 조회 성공", result);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/all")
	@Operation(summary = "전체 상품 목록 조회", description = "상품 상태와 관계 없이 전체 상품을 페이징으로 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "전체 상품 목록 조회 성공")
	})
	public ResponseEntity<ApiResponseDto<PagedResponseDto<ProductInfo>>> getAllProducts(
		@ParameterObject Pageable pageable
	) {
		Page<ProductInfo> page = productService.getProducts(pageable);

		PagedResponseDto<ProductInfo> paged = new PagedResponseDto<>(
			page.getContent(),
			new PageInfoDto(
				page.getNumber(),
				page.getSize(),
				page.getTotalElements(),
				page.getTotalPages()
			)
		);

		ApiResponseDto<PagedResponseDto<ProductInfo>> response =
			new ApiResponseDto<>(HttpStatus.OK.value(), "전체 상품 목록 조회 성공", paged);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/products")
	public ResponseEntity<ProductInfo> createProduct(
		@RequestHeader("Member-Id") UUID memberId,
		@RequestBody ProductRequest request
	) {

		ProductCommand command = request.toCreateCommand();

		ProductInfo info = productService.createProduct(memberId, command);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(info);
	}

	@PutMapping("/{productId}")
	public ResponseEntity<ProductInfo> updateProduct(
		@RequestHeader("Member-Id") UUID memberId,
		@PathVariable UUID productId,
		@RequestBody ProductRequest request
	) {
		ProductUpdateCommand command = request.toUpdateCommand();

		ProductInfo response = productService.updateProduct(memberId, productId, command);

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{id}/status")
	@Operation(summary = "상품 상태 변경", description = "상품 상태를 판매 중/일시 중단 등으로 변경합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 상태 변경 성공"),
		@ApiResponse(responseCode = "404", description = "해당 ID의 상품이 없습니다.")
	})
	public ResponseEntity<ApiResponseDto<ProductInfo>> updateProductStatus(
		@Parameter(description = "상품 ID") @PathVariable UUID id,
		@RequestBody StatusRequest request
	) {
		ProductStatus status = request.status();
		ProductInfo result = productService.updateStatus(id, status);

		ApiResponseDto<ProductInfo> response =
			new ApiResponseDto<>(HttpStatus.OK.value(), "상품 상태 변경 성공", result);

		return ResponseEntity.ok(response);
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

	@PostMapping("/presigned-url")
	public ResponseEntity<PresignedUrlResponse> createPresignedUrl(
		@RequestBody PresignedUrlRequest request
	) {
		PresignedUrlInfo info =
			productImageService.createUploadUrl(request.fileName(), request.contentType());

		PresignedUrlResponse response = new PresignedUrlResponse(
			info.url(),
			info.key()
		);

		return ResponseEntity.ok(response);
	}
}
