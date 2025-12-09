package com.node5.catalogservice.search.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.node5.catalogservice.search.application.dto.ProductSearchResponse;
import com.node5.catalogservice.search.domain.ProductDocument;
import com.node5.catalogservice.search.infrastrucutre.ProductSearchRepository;
import com.node5.catalogservice.search.presentation.dto.ProductSearchRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {

	private final ProductSearchRepository productSearchRepository;

	public Page<ProductSearchResponse> search(ProductSearchRequest request, Pageable pageable) {

		String keyword = request.keyword();
		String category = request.category();
		Integer minPrice = request.minPrice();
		Integer maxPrice = request.maxPrice();

		boolean hasKeyword = keyword != null && !keyword.isBlank();
		boolean hasCategory = category != null && !category.isBlank();
		boolean hasPriceRange = (minPrice != null && maxPrice != null);

		// Integer → Long 변환
		Long min = (minPrice != null) ? minPrice.longValue() : null;
		Long max = (maxPrice != null) ? maxPrice.longValue() : null;

		// TODO: shopId, sort는 1차 구현에서는 사용하지 않음
		// String shopId = request.shopId();
		// ProductSearchSort sort = request.sort();

		Page<ProductDocument> result;

		// 1) 가격 범위 없는 기존 분기
		if (!hasPriceRange) {
			if (!hasKeyword && !hasCategory) {
				result = productSearchRepository.findByStatus("ON_SALE", pageable);

			} else if (hasKeyword && !hasCategory) {
				result = productSearchRepository
					.findByStatusAndNameContainingIgnoreCase("ON_SALE", keyword, pageable);

			} else if (!hasKeyword && hasCategory) {
				result = productSearchRepository
					.findByStatusAndCategory("ON_SALE", category, pageable);

			} else { // keyword + category
				result = productSearchRepository
					.findByStatusAndNameContainingIgnoreCaseAndCategory(
						"ON_SALE", keyword, category, pageable
					);
			}

			// 2) 가격 범위 있는 경우
		} else {
			if (!hasKeyword && !hasCategory) {
				result = productSearchRepository
					.findByStatusAndPriceBetween("ON_SALE", min, max, pageable);

			} else if (hasKeyword && !hasCategory) {
				result = productSearchRepository
					.findByStatusAndNameContainingIgnoreCaseAndPriceBetween(
						"ON_SALE", keyword, min, max, pageable
					);

			} else if (!hasKeyword && hasCategory) {
				result = productSearchRepository
					.findByStatusAndCategoryAndPriceBetween(
						"ON_SALE", category, min, max, pageable
					);

			} else { // keyword + category + price range
				result = productSearchRepository
					.findByStatusAndNameContainingIgnoreCaseAndCategoryAndPriceBetween(
						"ON_SALE", keyword, category, min, max, pageable
					);
			}
		}

		// ES 도큐먼트에서 응답 DTO 변환
		return result.map(doc ->
			new ProductSearchResponse(
				doc.getProductId(),
				doc.getName(),
				doc.getCategory(),
				doc.getPrice(),
				doc.getStatus()
			)
		);
	}
}
