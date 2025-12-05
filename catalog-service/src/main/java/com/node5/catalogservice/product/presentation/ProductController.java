package com.node5.catalogservice.product.presentation;

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
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.product.application.ProductService;
import com.node5.catalogservice.product.application.dto.ProductInfo;
import com.node5.catalogservice.product.domain.ProductStatus;
import com.node5.catalogservice.product.presentation.dto.ProductRequest;
import com.node5.catalogservice.product.presentation.dto.StatusRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.v1}/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@GetMapping
	public ResponseEntity<Page<ProductInfo>> list(Pageable pageable) {
		Page<ProductInfo> result = productService.getOnSaleProducts(pageable);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductInfo> get(@PathVariable UUID id) {
		ProductInfo result = productService.getOnSaleProduct(id);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/all")
	public ResponseEntity<Page<ProductInfo>> listAll(Pageable pageable) {
		Page<ProductInfo> result = productService.getProducts(pageable);
		return ResponseEntity.ok(result);
	}

	@PostMapping
	public ResponseEntity<ProductInfo> create(@RequestBody ProductRequest request) {
		ProductInfo result = productService.createProduct(request.toCreateCommand());
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ProductInfo> update(@PathVariable UUID id, @RequestBody ProductRequest request) {
		ProductInfo result = productService.updateProduct(id, request.toUpdateCommand());
		return ResponseEntity.ok(result);
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<ProductInfo> updateStatus(@PathVariable UUID id, @RequestBody StatusRequest request) {
		ProductStatus status = request.status();
		ProductInfo result = productService.updateStatus(id, status);
		return ResponseEntity.ok(result);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> discontinue(@PathVariable UUID id) {
		productService.discontinueProduct(id);
		return ResponseEntity.noContent().build();
	}
}
