package com.node5.catalogservice.product.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

	Page<Product> findByStatus(ProductStatus status, Pageable pageable);

	Page<Product> findByShopId(UUID shopId, Pageable pageable);

	Optional<Product> findByIdAndStatus(UUID id, ProductStatus status);

	Optional<Product> findById(UUID id);

	Product save(Product product);

	List<Product> findAllByIdIn(Collection<UUID> ids);

	List<Product> findAllByIdInAndStatus(Collection<UUID> ids, ProductStatus status);

	int discontinueByShopId(UUID shopId);
}
