package com.node5.catalogservice.product.domain;

import java.util.Optional;

public interface ProductIdempotencyRepository {

	/**
	 * key로 PROCESSING 레코드를 선점 시도합니다.
	 * @return true면 선점 성공, false면 이미 존재
	 */
	boolean tryStartProcessing(String idempotencyKey);

	Optional<ProductIdempotency> findByKey(String idempotencyKey);

	ProductIdempotency save(ProductIdempotency entity);
}
