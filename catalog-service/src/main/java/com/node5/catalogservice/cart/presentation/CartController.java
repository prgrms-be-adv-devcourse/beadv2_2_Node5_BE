package com.node5.catalogservice.cart.presentation;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.cart.application.CartService;
import com.node5.catalogservice.cart.application.dto.CartItemInfo;
import com.node5.catalogservice.cart.presentation.dto.CartItemRequest;
import com.node5.catalogservice.cart.presentation.dto.CartItemUpdateRequest;
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
@RequestMapping("${api.v1}/carts")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "장바구니 관리 API")
public class CartController {

	private final CartService cartService;

	@GetMapping
	@Operation(summary = "장바구니 조회", description = "회원 ID로 장바구니 목록을 페이징 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "장바구니 조회 성공")
	})
	public ResponseEntity<ApiResponseDto<PagedResponseDto<CartItemInfo>>> getCartItems(
		@Parameter(description = "회원 ID (UUID 문자열)") @RequestParam("memberId") String memberId,
		@ParameterObject Pageable pageable
	) {
		UUID memberUuid = UUID.fromString(memberId);

		Page<CartItemInfo> page = cartService.getCartItems(memberUuid, pageable);

		PagedResponseDto<CartItemInfo> paged = new PagedResponseDto<>(
			page.getContent(),
			new PageInfoDto(
				page.getNumber(),
				page.getSize(),
				page.getTotalElements(),
				page.getTotalPages()
			)
		);

		return ResponseEntity.ok(
			new ApiResponseDto<>(HttpStatus.OK.value(), "장바구니 조회 성공", paged)
		);
	}

	@PostMapping
	@Operation(summary = "장바구니 상품 추가", description = "상품을 장바구니에 추가합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "상품 장바구니 추가 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다.")
	})
	public ResponseEntity<ApiResponseDto<CartItemInfo>> addCartItem(@RequestBody CartItemRequest request) {
		CartItemInfo result = cartService.addItem(request.toCommand());

		return ResponseEntity.status(HttpStatus.CREATED.value()).body(
			new ApiResponseDto<>(HttpStatus.CREATED.value(), "상품 장바구니 추가 성공", result)
		);
	}

	@PatchMapping("/{id}")
	@Operation(summary = "장바구니 수량 변경", description = "장바구니 항목의 수량을 변경합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "장바구니 수량 변경 성공"),
		@ApiResponse(responseCode = "404", description = "해당 ID의 장바구니 항목이 없습니다.")
	})
	public ResponseEntity<ApiResponseDto<CartItemInfo>> updateCartItem(
		@Parameter(description = "장바구니 항목 ID") @PathVariable("id") UUID id,
		@RequestBody CartItemUpdateRequest request
	) {
		CartItemInfo result = cartService.updateItem(id, request.toCommand());

		return ResponseEntity.ok(
			new ApiResponseDto<>(HttpStatus.OK.value(), "장바구니 수량 변경 성공", result)
		);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "장바구니 상품 삭제", description = "장바구니에서 특정 항목을 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "장바구니 상품 삭제 성공"),
		@ApiResponse(responseCode = "404", description = "해당 ID의 장바구니 항목이 없습니다.")
	})
	public ResponseEntity<Void> removeItem(@Parameter(description = "장바구니 항목 ID") @PathVariable("id") UUID id) {
		cartService.removeItem(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping
	@Operation(summary = "장바구니 비우기", description = "회원 ID로 장바구니를 모두 비웁니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "장바구니 비우기 성공")
	})
	public ResponseEntity<Void> clearCart(
		@Parameter(description = "회원 ID (UUID 문자열)") @RequestParam("memberId") String memberId
	) {
		cartService.clearCart(UUID.fromString(memberId));
		return ResponseEntity.noContent().build();
	}
}
