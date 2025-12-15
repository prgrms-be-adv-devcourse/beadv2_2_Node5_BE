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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.cart.application.CartService;
import com.node5.catalogservice.cart.application.dto.CartItemInfo;
import com.node5.catalogservice.cart.presentation.dto.CartItemRequest;
import com.node5.catalogservice.cart.presentation.dto.CartItemUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.v1}/carts")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "회원의 장바구니를 관리하는 API")
public class CartController {

	private final CartService cartService;

	@GetMapping
	@Operation(summary = "장바구니 조회", description = "회원의 장바구니 목록을 페이징 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "장바구니 조회 성공")
	})
	public ResponseEntity<Page<CartItemInfo>> getCartItems(
		@RequestHeader("Member-Id") UUID memberId,
		@ParameterObject Pageable pageable
	) {
		return ResponseEntity.ok(cartService.getCartItems(memberId, pageable));
	}

	@PostMapping
	@Operation(summary = "장바구니 상품 추가", description = "상품을 장바구니에 추가합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "상품 장바구니 추가 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다.")
	})
	public ResponseEntity<CartItemInfo> addCartItem(
		@RequestHeader("Member-Id") UUID memberId,
		@Valid @RequestBody CartItemRequest request
	) {
		CartItemInfo result = cartService.addItem(request.toCommand(memberId));
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@PatchMapping("/{cartItemId}")
	@Operation(summary = "장바구니 수량 변경", description = "장바구니 항목의 수량을 변경합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "장바구니 수량 변경 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다."),
		@ApiResponse(responseCode = "403", description = "해당 장바구니 항목에 대한 권한이 없습니다."),
		@ApiResponse(responseCode = "404", description = "해당 ID의 장바구니 항목이 없습니다.")
	})
	public ResponseEntity<CartItemInfo> updateCartItem(
		@RequestHeader("Member-Id") UUID memberId,
		@PathVariable UUID cartItemId,
		@Valid @RequestBody CartItemUpdateRequest request
	) {
		return ResponseEntity.ok(cartService.updateItem(memberId, cartItemId, request.toCommand()));
	}

	@DeleteMapping("/{cartItemId}")
	@Operation(summary = "장바구니 상품 삭제", description = "장바구니에서 특정 항목을 삭제합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "장바구니 상품 삭제 성공"),
		@ApiResponse(responseCode = "403", description = "해당 장바구니 항목에 대한 권한이 없습니다."),
		@ApiResponse(responseCode = "404", description = "해당 ID의 장바구니 항목이 없습니다.")
	})
	public ResponseEntity<Void> removeItem(
		@RequestHeader("Member-Id") UUID memberId,
		@PathVariable UUID cartItemId
	) {
		cartService.removeItem(memberId, cartItemId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping
	@Operation(summary = "장바구니 비우기", description = "회원의 장바구니를 모두 비웁니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "장바구니 비우기 성공")
	})
	public ResponseEntity<Void> clearCart(
		@RequestHeader("Member-Id") UUID memberId
	) {
		cartService.clearCart(memberId);
		return ResponseEntity.noContent().build();
	}
}
