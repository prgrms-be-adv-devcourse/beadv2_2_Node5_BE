package com.node5.supportservice.search.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.node5.common.exception.BaseException;
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

	public Page<ProductSearchResponse> search(ProductSearchCommand command, Pageable pageable) {
		validatePriceRange(command.minPrice(), command.maxPrice());

		String normalizedKeyword = queryNormalizer.normalize(command.keyword());
		ProductSearchCommand normalizedCommand = new ProductSearchCommand(
			normalizedKeyword,
			command.shopId(),
			command.category(),
			command.minPrice(),
			command.maxPrice(),
			command.sort()
		);

		Page<ProductDocument> page = productSearchPort.search(normalizedCommand, pageable);
		return page.map(ProductSearchResponse::from);
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
