package com.node5.catalogservice.cart.presentation;

import java.util.UUID;

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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.v1}/carts")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	@GetMapping
	public ResponseEntity<ApiResponseDto<PagedResponseDto<CartItemInfo>>> getCartItems(
		@RequestParam("memberId") String memberId,
		Pageable pageable
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
	public ResponseEntity<ApiResponseDto<CartItemInfo>> addCartItem(@RequestBody CartItemRequest request) {
		CartItemInfo result = cartService.addItem(request.toCommand());

		return ResponseEntity.status(HttpStatus.CREATED.value()).body(
			new ApiResponseDto<>(HttpStatus.CREATED.value(), "상품 장바구니 추가 성공", result)
		);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponseDto<CartItemInfo>> updateCartItem(
		@PathVariable("id") UUID id,
		@RequestBody CartItemUpdateRequest request
	) {
		CartItemInfo result = cartService.updateItem(id, request.toCommand());

		return ResponseEntity.ok(
			new ApiResponseDto<>(HttpStatus.OK.value(), "장바구니 수량 변경 성공", result)
		);
	}

	@PatchMapping("/{id}/decrease")
	public ResponseEntity<ApiResponseDto<CartItemInfo>> decreaseItem(
		@PathVariable("id") UUID id,
		@RequestBody CartItemUpdateRequest request
	) {
		CartItemInfo result = cartService.decreaseItem(id, request.toCommand());

		return ResponseEntity.ok(
			new ApiResponseDto<>(HttpStatus.OK.value(), "장바구니 수량 감소 성공", result)
		);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> removeItem(@PathVariable("id") UUID id) {
		cartService.removeItem(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping
	public ResponseEntity<Void> clearCart(@RequestParam("memberId") String memberId) {
		cartService.clearCart(UUID.fromString(memberId));
		return ResponseEntity.noContent().build();
	}
}
