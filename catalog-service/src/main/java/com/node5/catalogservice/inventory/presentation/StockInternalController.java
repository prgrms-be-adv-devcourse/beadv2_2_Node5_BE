package com.node5.catalogservice.inventory.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.inventory.application.InventoryService;
import com.node5.catalogservice.inventory.application.dto.StockReservationInfo;
import com.node5.catalogservice.inventory.presentation.dto.StockCommitRequest;
import com.node5.catalogservice.inventory.presentation.dto.StockHoldRequest;
import com.node5.catalogservice.inventory.presentation.dto.StockReleaseRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("internal/stocks/reservations")
@RequiredArgsConstructor
public class StockInternalController {

	private final InventoryService inventoryService;

	@PostMapping
	public ResponseEntity<StockReservationInfo> hold(@Valid @RequestBody StockHoldRequest request) {
		StockReservationInfo result = inventoryService.hold(request.toCommand());
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@PostMapping("/commit")
	public ResponseEntity<Void> commit(@Valid @RequestBody StockCommitRequest request) {
		inventoryService.commit(request.toCommand());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/release")
	public ResponseEntity<Void> release(@Valid @RequestBody StockReleaseRequest request) {
		inventoryService.release(request.toCommand());
		return ResponseEntity.ok().build();
	}
}
