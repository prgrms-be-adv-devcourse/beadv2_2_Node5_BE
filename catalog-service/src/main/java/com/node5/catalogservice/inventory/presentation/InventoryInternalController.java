package com.node5.catalogservice.inventory.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.inventory.application.InventoryService;
import com.node5.catalogservice.inventory.application.dto.StockHoldBatchResult;
import com.node5.catalogservice.inventory.presentation.dto.StockCommitRequest;
import com.node5.catalogservice.inventory.presentation.dto.StockHoldBatchRequest;
import com.node5.catalogservice.inventory.presentation.dto.StockReleaseRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/stocks/reservations")
@RequiredArgsConstructor
@Tag(name = "Inventory Internal", description = "주문/결제 흐름을 위한 내부 재고 예약 처리 API")
public class InventoryInternalController {

	private final InventoryService inventoryService;

	@PostMapping("/hold")
	@Operation(
		summary = "재고 선점",
		description = "주문 생성 전에 재고를 임시로 차감하여 동시 구매로 인한 초과 판매를 방지합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "재고 선점 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다."),
		@ApiResponse(responseCode = "404", description = "상품 재고가 존재하지 않습니다.")
	})
	public ResponseEntity<StockHoldBatchResult> hold(@Valid @RequestBody StockHoldBatchRequest request) {
		var result = inventoryService.holdBatch(request.toCommand());
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@PostMapping("/commit")
	@Operation(
		summary = "재고 확정",
		description = "결제가 완료된 주문에 대해 선점된 재고를 확정 처리합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "재고 확정 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다."),
		@ApiResponse(responseCode = "404", description = "재고 예약이 존재하지 않습니다.")
	})
	public ResponseEntity<Void> commit(@Valid @RequestBody StockCommitRequest request) {
		inventoryService.commit(request.toCommand());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/release")
	@Operation(
		summary = "재고 해제",
		description = "주문 실패 또는 취소 시 선점된 재고를 복구합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "재고 해제 성공"),
		@ApiResponse(responseCode = "400", description = "요청 값이 유효하지 않습니다."),
		@ApiResponse(responseCode = "404", description = "재고 예약이 존재하지 않습니다.")
	})
	public ResponseEntity<Void> release(@Valid @RequestBody StockReleaseRequest request) {
		inventoryService.release(request.toCommand());
		return ResponseEntity.ok().build();
	}
}
