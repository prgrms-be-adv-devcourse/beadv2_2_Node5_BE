package com.node5.catalogservice.product.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductRepository;
import com.node5.catalogservice.product.domain.ProductStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

	private final ProductJpaRepository productJpaRepository;

	@Override
	public Page<Product> findByStatus(ProductStatus status, Pageable pageable) {
		return productJpaRepository.findByStatus(status, pageable);
	}

	@Override
	public Optional<Product> findByIdAndStatus(UUID id, ProductStatus status) {
		return productJpaRepository.findByIdAndStatus(id, status);
	}

	@Override
	public Optional<Product> findById(UUID id) {
		return productJpaRepository.findById(id);
	}

	@Override
	public Product save(Product product) {
		return productJpaRepository.save(product);
	}

	@Override
	public Page<Product> findAll(Pageable pageable) {
		return productJpaRepository.findAll(pageable);
	}
}
