package com.node5.supportservice.search.application;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.node5.common.exception.BaseException;
import com.node5.supportservice.config.SearchSponsoredProperties;
import com.node5.supportservice.search.application.dto.ProductSearchCommand;
import com.node5.supportservice.search.application.dto.ProductSearchResponse;
import com.node5.supportservice.search.application.port.ProductSearchPort;
import com.node5.supportservice.search.application.query.QueryNormalizer;
import com.node5.supportservice.search.domain.ProductDocument;
import com.node5.supportservice.search.exception.SearchErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

	private final ProductSearchPort productSearchPort;
	private final QueryNormalizer queryNormalizer;
	private final SearchSponsoredProperties sponsoredProps;

	public Page<ProductSearchResponse> search(ProductSearchCommand command, Pageable pageable) {
		validatePriceRange(command.minPrice(), command.maxPrice());

		ProductSearchCommand normalizedCommand = normalize(command);

		int limit = sponsoredProps.getLimit();
		List<ProductDocument> sponsored = productSearchPort.searchSponsored(normalizedCommand, limit);

		Page<ProductDocument> normalPage = productSearchPort.search(normalizedCommand, pageable);

		Set<String> sponsoredIds = sponsored.stream()
			.map(ProductDocument::getProductId)
			.collect(Collectors.toSet());

		List<ProductDocument> normalWithoutSponsored = normalPage.getContent().stream()
			.filter(doc -> !sponsoredIds.contains(doc.getProductId()))
			.toList();

		List<ProductDocument> merged = mergePreserveOrder(sponsored, normalWithoutSponsored);
		Page<ProductDocument> mergedPage = new PageImpl<>(merged, pageable, normalPage.getTotalElements());

		return mergedPage.map(ProductSearchResponse::from);
	}

	private ProductSearchCommand normalize(ProductSearchCommand command) {
		String normalizedKeyword = queryNormalizer.normalize(command.keyword());
		return new ProductSearchCommand(
			normalizedKeyword,
			command.shopId(),
			command.category(),
			command.minPrice(),
			command.maxPrice(),
			command.sort()
		);
	}

	private List<ProductDocument> mergePreserveOrder(List<ProductDocument> sponsored, List<ProductDocument> normal) {
		LinkedHashMap<String, ProductDocument> map = new LinkedHashMap<>();
		for (ProductDocument d : sponsored) {
			map.put(d.getProductId(), d);
		}
		for (ProductDocument d : normal) {
			map.put(d.getProductId(), d);
		}
		return List.copyOf(map.values());
	}

	private void validatePriceRange(Integer minPrice, Integer maxPrice) {
		if ((minPrice == null) != (maxPrice == null)) {
			throw new BaseException(SearchErrorCode.PRICE_RANGE_INCOMPLETE);
		}
		if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
			throw new BaseException(SearchErrorCode.INVALID_PRICE_RANGE);
		}
	}
}
