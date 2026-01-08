package com.node5.catalogservice.search.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.node5.catalogservice.search.application.dto.ProductAutocompleteCommand;
import com.node5.catalogservice.search.application.port.ProductSearchPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductAutocompleteService {

	private static final int MIN_LENGTH = 2;

	private final ProductSearchPort productSearchPort;

	public List<String> autocomplete(String keyword) {
		if (!StringUtils.hasText(keyword)) return List.of();

		String trimmed = keyword.trim();
		if (trimmed.length() < MIN_LENGTH) return List.of();

		List<String> raw = productSearchPort.autocomplete(
			new ProductAutocompleteCommand(trimmed)
		);

		return raw.stream()
			.filter(StringUtils::hasText)
			.map(String::trim)
			.filter(s -> !s.isBlank())
			.distinct()
			.toList();
	}
}
