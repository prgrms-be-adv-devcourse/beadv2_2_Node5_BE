package com.node5.catalogservice.search.presentation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.search.application.SearchService;
import com.node5.catalogservice.search.application.dto.ProductSearchResponse;
import com.node5.catalogservice.search.domain.ProductSearchSort;
import com.node5.catalogservice.search.presentation.dto.ProductSearchRequest;
import com.node5.common.domain.ApiResponseDto;
import com.node5.common.domain.PageInfoDto;
import com.node5.common.domain.PagedResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.v1}/search")
@RequiredArgsConstructor
public class ProductSearchController {

	private final SearchService searchService;

	@GetMapping("/products")
	public ResponseEntity<ApiResponseDto<PagedResponseDto<ProductSearchResponse>>> searchProducts(
		String keyword,
		String category,
		String shopId,
		Integer minPrice,
		Integer maxPrice,
		ProductSearchSort sort,
		Pageable pageable
	) {
		// presentation DTO로 묶기
		ProductSearchRequest request = new ProductSearchRequest(
			keyword, category, shopId, minPrice, maxPrice, sort
		);

		// 서비스 호출
		Page<ProductSearchResponse> page = searchService.search(request, pageable);

		// 공통 페이지 응답 DTO로 래핑
		PagedResponseDto<ProductSearchResponse> paged =
			new PagedResponseDto<>(
				page.getContent(),
				new PageInfoDto(
					page.getNumber(),
					page.getSize(),
					page.getTotalElements(),
					page.getTotalPages()
				)
			);

		ApiResponseDto<PagedResponseDto<ProductSearchResponse>> response =
			new ApiResponseDto<>(
				HttpStatus.OK.value(),
				"상품 검색 성공",
				paged
			);

		return ResponseEntity.ok(response);
	}
}
