package com.node5.supportservice.search.presentation;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.node5.supportservice.search.application.ProductSearchService;
import com.node5.supportservice.search.application.dto.ProductCategoryCode;
import com.node5.supportservice.search.application.dto.ProductSearchCommand;
import com.node5.supportservice.search.application.dto.ProductSearchResponse;
import com.node5.supportservice.search.domain.ProductSearchSort;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.v1}/products/search")
@RequiredArgsConstructor
@Tag(name = "Product Search", description = "사용자를 위한 상품 검색 API")
public class ProductSearchController {

	private final ProductSearchService productSearchService;

	@GetMapping
	@Operation(
		summary = "상품 검색",
		description = "키워드, 카테고리, 가격 범위, 정렬 조건을 조합하여 판매 중 상품을 검색합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 검색 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다.")
	})
	public ResponseEntity<Page<ProductSearchResponse>> searchProducts(
		@Parameter(description = "검색 키워드")
		@RequestParam(required = false) String keyword,

		@Parameter(description = "카테고리", schema = @Schema(implementation = ProductCategoryCode.class))
		@RequestParam(required = false) ProductCategoryCode category,

		@Parameter(description = "최소 가격")
		@RequestParam(required = false) Integer minPrice,

		@Parameter(description = "최대 가격")
		@RequestParam(required = false) Integer maxPrice,

		@Parameter(description = "정렬 기준")
		@RequestParam(name = "searchSort", required = false) ProductSearchSort searchSort,

		@Parameter(description = "상점 ID(판매자)")
		@RequestParam(required = false) UUID shopId,

		@ParameterObject Pageable pageable
	) {
		ProductSearchCommand command = new ProductSearchCommand(
			keyword, shopId, category, minPrice, maxPrice, searchSort
		);

		return ResponseEntity.ok(productSearchService.search(command, pageable));
	}
}
