package com.node5.catalogservice.search.application.dto;

import java.util.UUID;

import com.node5.catalogservice.product.domain.ProductCategory;
import com.node5.catalogservice.search.domain.ProductSearchSort;

/**
 * 상품 검색 유스케이스의 입력 조건을 표현하는 Command 객체입니다.
 * <p>
 * Controller에서 생성되어 application 계층(Service)으로 전달됩니다.
 */
public record ProductSearchCommand(
	String keyword,
	UUID shopId,
	ProductCategory category,
	Integer minPrice,
	Integer maxPrice,
	ProductSearchSort sort
) {
}
