package com.node5.catalogservice.search.domain;

import org.springframework.data.domain.Sort;

public enum ProductSearchSort {
	LATEST,
	LOW_PRICE,
	HIGH_PRICE;

	public Sort toSort() {
		return switch (this) {
			case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
			case LOW_PRICE -> Sort.by(Sort.Direction.ASC, "price");
			case HIGH_PRICE -> Sort.by(Sort.Direction.DESC, "price");
		};
	}
}
