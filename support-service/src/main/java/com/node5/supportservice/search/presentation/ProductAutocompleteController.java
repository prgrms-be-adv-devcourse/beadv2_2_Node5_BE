package com.node5.supportservice.search.presentation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.node5.supportservice.search.application.ProductAutocompleteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/products/autocomplete")
@Tag(name = "Product Autocomplete", description = "사용자를 위한 상품 자동완성 API")
public class ProductAutocompleteController {

	private final ProductAutocompleteService productAutocompleteService;

	@GetMapping
	@Operation(summary = "상품 자동완성", description = "입력한 키워드를 기준으로 상품 자동완성 후보를 반환합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 자동완성 조회 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다.")
	})
	public ResponseEntity<List<String>> autocomplete(
		@Parameter(description = "검색 키워드")
		@RequestParam String keyword
	) {
		return ResponseEntity.ok(productAutocompleteService.autocomplete(keyword));
	}
}
