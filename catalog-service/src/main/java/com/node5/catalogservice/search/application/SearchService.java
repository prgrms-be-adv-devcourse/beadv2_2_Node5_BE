package com.node5.catalogservice.search.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.node5.catalogservice.search.application.dto.ProductSearchResponse;
import com.node5.catalogservice.search.domain.ProductDocument;
import com.node5.catalogservice.search.domain.ProductSearchSort;
import com.node5.catalogservice.search.infrastructure.ProductSearchRepository;
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
		ProductSearchSort sort = request.sort();

		boolean hasKeyword = keyword != null && !keyword.isBlank();
		boolean hasCategory = category != null && !category.isBlank();
		boolean hasPriceRange = minPrice != null && maxPrice != null;

		// 정렬 포함된 pageable 생성
		Pageable sortedPageable = PageRequest.of(
			pageable.getPageNumber(),
			pageable.getPageSize(),
			(sort == null ? ProductSearchSort.LATEST : sort).toSort()
		);

		Page<ProductDocument> result;

		// 1) 가격 범위 없는 경우
		if (!hasPriceRange) {
			if (!hasKeyword && !hasCategory) {
				result = productSearchRepository.findByStatus("ON_SALE", sortedPageable);

			} else if (hasKeyword && !hasCategory) {
				result = productSearchRepository.findByStatusAndNameContainingIgnoreCase(
					"ON_SALE", keyword, sortedPageable);

			} else if (!hasKeyword && hasCategory) {
				result = productSearchRepository.findByStatusAndCategory(
					"ON_SALE", category, sortedPageable);

			} else {
				result = productSearchRepository.findByStatusAndNameContainingIgnoreCaseAndCategory(
					"ON_SALE", keyword, category, sortedPageable);
			}

			// 2) 가격 범위 있는 경우
		} else {
			if (!hasKeyword && !hasCategory) {
				result = productSearchRepository.findByStatusAndPriceBetween(
					"ON_SALE", minPrice, maxPrice, sortedPageable);

			} else if (hasKeyword && !hasCategory) {
				result = productSearchRepository.findByStatusAndNameContainingIgnoreCaseAndPriceBetween(
					"ON_SALE", keyword, minPrice, maxPrice, sortedPageable);

			} else if (!hasKeyword && hasCategory) {
				result = productSearchRepository.findByStatusAndCategoryAndPriceBetween(
					"ON_SALE", category, minPrice, maxPrice, sortedPageable);

			} else {
				result = productSearchRepository.findByStatusAndNameContainingIgnoreCaseAndCategoryAndPriceBetween(
					"ON_SALE", keyword, category, minPrice, maxPrice, sortedPageable);
			}
		}

		// ES 도큐먼트 → 응답 DTO 변환
		return result.map(doc ->
			new ProductSearchResponse(
				doc.getProductId(),
				doc.getName(),
				doc.getCategory(),
				doc.getThumbnailUrl(),
				doc.getPrice(),
				doc.getStatus(),
				doc.getCreatedAt()
			)
		);
	}
}
