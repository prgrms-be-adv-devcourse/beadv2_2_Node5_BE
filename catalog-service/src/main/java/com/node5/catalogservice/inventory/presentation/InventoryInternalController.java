package com.node5.catalogservice.inventory.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.inventory.application.InventoryReservationService;
import com.node5.catalogservice.inventory.application.dto.StockHoldBatchResult;
import com.node5.catalogservice.inventory.presentation.dto.StockCommitBatchRequest;
import com.node5.catalogservice.inventory.presentation.dto.StockHoldBatchRequest;
import com.node5.catalogservice.inventory.presentation.dto.StockReleaseBatchRequest;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/stocks/reservations")
@RequiredArgsConstructor
@Hidden
public class InventoryInternalController {

	private final InventoryReservationService inventoryReservationService;

	@PostMapping("/hold")
	public ResponseEntity<StockHoldBatchResult> hold(@Valid @RequestBody StockHoldBatchRequest request) {
		var result = inventoryReservationService.holdBatch(request.toCommand());
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@PostMapping("/commit")
	public ResponseEntity<Void> commit(@Valid @RequestBody StockCommitBatchRequest request) {
		inventoryReservationService.commitBatch(request.toCommand());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/release")
	public ResponseEntity<Void> release(@Valid @RequestBody StockReleaseBatchRequest request) {
		inventoryReservationService.releaseBatch(request.toCommand());
		return ResponseEntity.ok().build();
	}
}
