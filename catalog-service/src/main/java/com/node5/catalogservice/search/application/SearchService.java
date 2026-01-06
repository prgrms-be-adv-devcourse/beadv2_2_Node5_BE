package com.node5.catalogservice.search.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.node5.catalogservice.search.application.dto.ProductSearchCommand;
import com.node5.catalogservice.search.application.dto.ProductSearchResponse;
import com.node5.catalogservice.search.application.port.ProductSearchPort;
import com.node5.catalogservice.search.domain.ProductDocument;
import com.node5.catalogservice.search.exception.SearchErrorCode;
import com.node5.catalogservice.search.exception.SearchException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {

	private final ProductSearchPort productSearchPort;

	public Page<ProductSearchResponse> search(ProductSearchCommand command, Pageable pageable) {
		validatePriceRange(command.minPrice(), command.maxPrice());

		Page<ProductDocument> page = productSearchPort.search(command, pageable);
		return page.map(ProductSearchResponse::from);
	}

	private void validatePriceRange(Integer minPrice, Integer maxPrice) {
		if ((minPrice == null) != (maxPrice == null)) {
			throw new SearchException(SearchErrorCode.PRICE_RANGE_INCOMPLETE);
		}
		if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
			throw new SearchException(SearchErrorCode.INVALID_PRICE_RANGE);
		}
	}
}
