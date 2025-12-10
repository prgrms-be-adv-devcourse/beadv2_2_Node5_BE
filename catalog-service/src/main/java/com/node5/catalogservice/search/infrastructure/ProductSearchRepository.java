package com.node5.catalogservice.search.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.node5.catalogservice.search.domain.ProductDocument;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {

	/**
	 * 가격 범위 없는 경우
	 */

	// 키워드 X, 카테고리 X
	Page<ProductDocument> findByStatus(String status, Pageable pageable);

	// 키워드 O, 카테고리 X
	Page<ProductDocument> findByStatusAndNameContainingIgnoreCase(
		String status, String name, Pageable pageable);

	// 키워드 X, 카테고리 O
	Page<ProductDocument> findByStatusAndCategory(
		String status, String category, Pageable pageable);

	// 키워드 O, 카테고리 O
	Page<ProductDocument> findByStatusAndNameContainingIgnoreCaseAndCategory(
		String status, String name, String category, Pageable pageable);

	/**
	 * 가격 범위 있는 경우
	 */

	// 키워드 X, 카테고리 X
	Page<ProductDocument> findByStatusAndPriceBetween(
		String status, Integer minPrice, Integer maxPrice, Pageable pageable);

	// 키워드 O, 카테고리 X
	Page<ProductDocument> findByStatusAndNameContainingIgnoreCaseAndPriceBetween(
		String status, String keyword, Integer minPrice, Integer maxPrice, Pageable pageable);

	// 키워드 X, 카테고리 O
	Page<ProductDocument> findByStatusAndCategoryAndPriceBetween(
		String status, String category, Integer minPrice, Integer maxPrice, Pageable pageable);

	// 키워드 O, 카테고리 O
	Page<ProductDocument> findByStatusAndNameContainingIgnoreCaseAndCategoryAndPriceBetween(
		String status, String keyword, String category, Integer minPrice, Integer maxPrice, Pageable pageable);
}
