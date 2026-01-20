package com.node5.supportservice.search.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.node5.supportservice.search.application.dto.ProductAutocompleteCommand;
import com.node5.supportservice.search.application.port.ProductSearchPort;
import com.node5.supportservice.search.application.query.QueryNormalizer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductAutocompleteService {

	private static final int MIN_LENGTH = 2;

	private final ProductSearchPort productSearchPort;
	private final QueryNormalizer queryNormalizer;

	public List<String> autocomplete(String keyword) {
		if (!StringUtils.hasText(keyword)) return List.of();

		String normalized = queryNormalizer.normalize(keyword);
		if (normalized.isBlank()) return List.of();
		if (normalized.length() < MIN_LENGTH) return List.of();

		List<String> raw = productSearchPort.autocomplete(new ProductAutocompleteCommand(normalized));

		return raw.stream()
			.filter(StringUtils::hasText)
			.map(String::trim)
			.filter(s -> !s.isBlank())
			.distinct()
			.toList();
	}
}
