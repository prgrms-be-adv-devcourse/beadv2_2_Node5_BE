package com.node5.catalogservice.inventory.presentation;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.inventory.application.InventoryService;
import com.node5.catalogservice.inventory.presentation.dto.StockResponse;
import com.node5.catalogservice.inventory.presentation.dto.StockUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.v1}/stocks")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "상품 재고를 관리하는 API")
public class InventoryController {

	private final InventoryService inventoryService;

	@PutMapping("/{productId}")
	@Operation(summary = "상품 재고 변경", description = "상품 재고 수량을 변경합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 재고 변경 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다."),
		@ApiResponse(responseCode = "403", description = "해당 상품에 대한 권한이 없습니다."),
		@ApiResponse(responseCode = "404", description = "상품 또는 상점 정보를 찾을 수 없습니다."),
		@ApiResponse(responseCode = "503", description = "상점 서비스를 사용할 수 없습니다.")
	})
	public ResponseEntity<StockResponse> updateStock(
		@RequestHeader("Member-Id") UUID memberId,
		@PathVariable UUID productId,
		@Valid @RequestBody StockUpdateRequest request
	) {
		StockResponse response = inventoryService.updateStockQuantity(memberId, productId, request.quantity());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{productId}")
	@Operation(summary = "상품 재고 조회", description = "상품 ID로 재고를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 재고 조회 성공"),
		@ApiResponse(responseCode = "404", description = "상품 재고가 존재하지 않습니다.")
	})
	public ResponseEntity<StockResponse> getStock(@PathVariable UUID productId) {
		StockResponse response = inventoryService.getStock(productId);
		return ResponseEntity.ok(response);
	}
}
