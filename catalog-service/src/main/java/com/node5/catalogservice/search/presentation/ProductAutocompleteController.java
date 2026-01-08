package com.node5.catalogservice.search.presentation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.node5.catalogservice.product.domain.ProductCategory;
import com.node5.catalogservice.search.application.ProductAutocompleteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.v1}/products/autocomplete")
public class ProductAutocompleteController {

	private final ProductAutocompleteService productAutocompleteService;

	@GetMapping
	public ResponseEntity<List<String>> autocomplete(
		@RequestParam String keyword,
		@RequestParam(required = false) ProductCategory category
		) {
		return ResponseEntity.ok(productAutocompleteService.autocomplete(keyword, category));
	}
}
