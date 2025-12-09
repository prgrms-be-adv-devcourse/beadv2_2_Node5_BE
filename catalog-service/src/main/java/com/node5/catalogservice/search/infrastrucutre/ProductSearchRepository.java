package com.node5.catalogservice.search.infrastrucutre;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.node5.catalogservice.search.domain.ProductDocument;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {

	// 1단계: 기본 (status만)
	Page<ProductDocument> findByStatus(String status, Pageable pageable);

	// 2단계: 키워드만
	Page<ProductDocument> findByStatusAndNameContainingIgnoreCase(
		String status,
		String name,
		Pageable pageable
	);

	// 3단계: 카테고리만
	Page<ProductDocument> findByStatusAndCategory(
		String status,
		String category,
		Pageable pageable
	);

	// 3단계: 키워드 + 카테고리 같이 쓸 때
	Page<ProductDocument> findByStatusAndNameContainingIgnoreCaseAndCategory(
		String status,
		String name,
		String category,
		Pageable pageable
	);

	// 4단계: 가격 범위 추가 버전들 (Long 사용)
	Page<ProductDocument> findByStatusAndPriceBetween(
		String status,
		Long minPrice,
		Long maxPrice,
		Pageable pageable
	);

	Page<ProductDocument> findByStatusAndNameContainingIgnoreCaseAndPriceBetween(
		String status,
		String name,
		Long minPrice,
		Long maxPrice,
		Pageable pageable
	);

	Page<ProductDocument> findByStatusAndCategoryAndPriceBetween(
		String status,
		String category,
		Long minPrice,
		Long maxPrice,
		Pageable pageable
	);

	Page<ProductDocument> findByStatusAndNameContainingIgnoreCaseAndCategoryAndPriceBetween(
		String status,
		String name,
		String category,
		Long minPrice,
		Long maxPrice,
		Pageable pageable
	);
}
