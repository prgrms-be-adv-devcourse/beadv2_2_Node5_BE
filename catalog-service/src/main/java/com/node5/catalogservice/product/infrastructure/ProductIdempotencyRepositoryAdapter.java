package com.node5.catalogservice.product.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.node5.catalogservice.product.domain.ProductIdempotency;
import com.node5.catalogservice.product.domain.ProductIdempotencyRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductIdempotencyRepositoryAdapter implements ProductIdempotencyRepository {

	private final ProductIdempotencyJpaRepository jpaRepository;

	@Override
	@Transactional
	public boolean tryStartProcessing(String idempotencyKey) {
		return jpaRepository.tryInsertProcessing(idempotencyKey) == 1;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ProductIdempotency> findByKey(String idempotencyKey) {
		return jpaRepository.findById(idempotencyKey);
	}

	@Override
	@Transactional
	public ProductIdempotency save(ProductIdempotency entity) {
		return jpaRepository.save(entity);
	}
}
