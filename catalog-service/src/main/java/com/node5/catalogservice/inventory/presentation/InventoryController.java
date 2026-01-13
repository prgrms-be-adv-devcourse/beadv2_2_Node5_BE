package com.node5.catalogservice.inventory.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.node5.catalogservice.inventory.application.InventoryService;
import com.node5.catalogservice.inventory.presentation.dto.StockRegisterRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.v1}/stocks")
@RequiredArgsConstructor
public class InventoryController {

	private final InventoryService inventoryService;

	@PostMapping
	public ResponseEntity<Void> register(@Valid @RequestBody StockRegisterRequest request) {
		inventoryService.registerStock(request.toCommand());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
