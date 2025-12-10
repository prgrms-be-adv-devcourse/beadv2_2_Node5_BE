package com.node5.catalogservice.product.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

	Page<Product> findByStatus(ProductStatus status, Pageable pageable);

	Optional<Product> findByIdAndStatus(UUID id, ProductStatus status);

	Optional<Product> findById(UUID id);

	Product save(Product product);

	Page<Product> findAll(Pageable pageable);
}
