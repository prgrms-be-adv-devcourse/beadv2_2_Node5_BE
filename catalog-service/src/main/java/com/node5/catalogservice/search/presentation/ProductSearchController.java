package com.node5.catalogservice.search.presentation;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.product.domain.ProductCategory;
import com.node5.catalogservice.search.application.SearchService;
import com.node5.catalogservice.search.application.dto.ProductSearchResponse;
import com.node5.catalogservice.search.domain.ProductSearchSort;
import com.node5.catalogservice.search.presentation.dto.ProductSearchRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.v1}/search")
@RequiredArgsConstructor
@Tag(name = "Product Search", description = "상품 검색 API")
public class ProductSearchController {

	private final SearchService searchService;

	@GetMapping("/products")
	@Operation(
		summary = "상품 검색",
		description = "키워드, 카테고리, 가격 범위, 정렬 조건을 조합하여 판매 중 상품을 검색합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 검색 성공")
	})
	public ResponseEntity<Page<ProductSearchResponse>> searchProducts(
		@Parameter(description = "검색 키워드", required = false)
		@RequestParam(required = false) String keyword,

		@Parameter(
			description = "카테고리",
			required = false,
			schema = @Schema(implementation = ProductCategory.class)
		)
		@RequestParam(required = false) ProductCategory category,

		@Parameter(description = "최소 가격", required = false)
		@RequestParam(required = false) Integer minPrice,

		@Parameter(description = "최대 가격", required = false)
		@RequestParam(required = false) Integer maxPrice,

		@Parameter(description = "정렬 기준", required = false)
		@RequestParam(required = false) ProductSearchSort sort,

		@ParameterObject Pageable pageable
	) {
		ProductSearchRequest request =
			new ProductSearchRequest(keyword, category, minPrice, maxPrice, sort);

		return ResponseEntity.ok(
			searchService.search(request, pageable)
		);
	}
}
