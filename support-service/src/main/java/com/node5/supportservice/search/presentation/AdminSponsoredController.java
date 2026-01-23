package com.node5.supportservice.search.presentation;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.supportservice.search.application.SponsoredProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/sponsored-products")
@RequiredArgsConstructor
@Tag(name = "Admin Sponsored Product", description = "검색 결과 노출 정책을 관리하기 위한 관리자 API")
public class AdminSponsoredController {

	private final SponsoredProductService sponsoredProductService;

	@PostMapping("/{productId}")
	@Operation(
		summary = "스폰서 상품 지정", description = "지정한 상품을 검색 결과 상단에 노출되도록 설정합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "성공")
	})
	public void markAsSponsored(@PathVariable UUID productId) {
		sponsoredProductService.sponsor(productId);
	}

	@DeleteMapping("/{productId}")
	@Operation(
		summary = "스폰서 상품 해제", description = "지정한 상품의 검색 결과 상단 노출을 해제합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "성공")
	})
	public void unmarkAsSponsored(@PathVariable UUID productId) {
		sponsoredProductService.unsponsor(productId);
	}
}
