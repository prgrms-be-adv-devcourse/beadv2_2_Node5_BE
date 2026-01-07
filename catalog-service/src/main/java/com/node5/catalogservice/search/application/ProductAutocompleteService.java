package com.node5.catalogservice.search.application;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.node5.catalogservice.product.domain.ProductCategory;
import com.node5.catalogservice.search.application.dto.ProductAutocompleteCommand;
import com.node5.catalogservice.search.application.port.ProductSearchPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductAutocompleteService {

	private static final int MIN_LENGTH = 2;
	private static final int DEFAULT_SIZE = 10;
	private static final int MAX_SIZE = 20;

	private final ProductSearchPort productSearchPort;

	public List<String> autocomplete(String keyword, ProductCategory category, Integer size) {
		if (!StringUtils.hasText(keyword)) {
			return List.of();
		}

		String trimmed = keyword.trim();
		if (trimmed.length() < MIN_LENGTH) {
			return List.of();
		}

		int normalizedSize = normalizeSize(size);

		List<String> raw = productSearchPort.autocomplete(
			new ProductAutocompleteCommand(trimmed, category, normalizedSize)
		);

		// 중복 제거 + 순서 유지
		Set<String> dedup = new LinkedHashSet<>(raw);
		return dedup.stream().limit(normalizedSize).toList();
	}

	private int normalizeSize(Integer size) {
		if (size == null || size < 1) return DEFAULT_SIZE;
		return Math.min(size, MAX_SIZE);
	}
}
