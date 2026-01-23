package com.node5.supportservice.search.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.supportservice.search.application.reindex.ProductReindexService;
import com.node5.supportservice.search.application.reindex.ReindexStatus;
import com.node5.supportservice.search.application.reindex.ReindexStatusStore;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/search/reindex")
@RequiredArgsConstructor
@Hidden
public class SearchReindexInternalController {

	private final ProductReindexService reindexService;
	private final ReindexStatusStore statusStore;

	@PostMapping("/products")
	public void reindexProducts() {
		reindexService.reindexAll();
	}

	@GetMapping("/products")
	public ReindexStatus status() {
		return statusStore.snapshot();
	}
}
