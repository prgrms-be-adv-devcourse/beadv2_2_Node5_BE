package com.node5.supportservice.search.presentation;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.node5.supportservice.search.application.SponsoredProductService;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/sponsored-products")
@RequiredArgsConstructor
@Hidden
public class SearchInternalController {

	private final SponsoredProductService sponsoredProductService;

	@PostMapping("/{productId}")
	public void markAsSponsored(@PathVariable UUID productId) {
		sponsoredProductService.sponsor(productId);
	}

	@DeleteMapping("/{productId}")
	public void unmarkAsSponsored(@PathVariable UUID productId) {
		sponsoredProductService.unsponsor(productId);
	}
}
