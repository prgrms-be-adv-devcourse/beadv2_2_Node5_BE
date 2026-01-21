package com.node5.catalogservice.product.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.node5.catalogservice.product.domain.ProductIdempotency;

public interface ProductIdempotencyJpaRepository extends JpaRepository<ProductIdempotency, String> {

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query(value = """
		INSERT INTO catalog.product_idempotency (idempotency_key, status)
		VALUES (:key, 'PROCESSING')
		ON CONFLICT (idempotency_key) DO NOTHING
		""", nativeQuery = true)
	int tryInsertProcessing(@Param("key") String key);
}
