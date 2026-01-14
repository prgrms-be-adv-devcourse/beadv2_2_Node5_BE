package com.node5.catalogservice.inventory.presentation;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.inventory.application.InventoryService;
import com.node5.catalogservice.inventory.presentation.dto.StockRegisterRequest;
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

	@PostMapping
	@Operation(summary = "상품 재고 등록", description = "상품 재고를 신규 등록합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "상품 재고 등록 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다.")
	})
	public ResponseEntity<Void> register(@Valid @RequestBody StockRegisterRequest request) {
		inventoryService.registerStock(request.toCommand());
		return ResponseEntity.status(HttpStatus.CREATED).build();
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

	@PutMapping("/{productId}")
	@Operation(summary = "상품 재고 변경", description = "상품 재고 수량을 변경합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "상품 재고 변경 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다."),
		@ApiResponse(responseCode = "404", description = "상품 재고가 존재하지 않습니다.")
	})
	public ResponseEntity<StockResponse> updateStock(
		@PathVariable UUID productId,
		@Valid @RequestBody StockUpdateRequest request
	) {
		StockResponse response = inventoryService.updateStockQuantity(
			productId,
			request.quantity()
		);
		return ResponseEntity.ok(response);
	}
}
