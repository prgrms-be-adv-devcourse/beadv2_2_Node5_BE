package com.node5.catalogservice.product.infrastructure;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductStatus;

public interface ProductJpaRepository extends JpaRepository<Product, UUID> {

	Page<Product> findByStatus(ProductStatus status, Pageable pageable);

	Page<Product> findByShopId(UUID shopId, Pageable pageable);

	Optional<Product> findByIdAndStatus(UUID id, ProductStatus status);

	List<Product> findByIdIn(Collection<UUID> ids);

	List<Product> findByIdInAndStatus(Collection<UUID> ids, ProductStatus status);
}
