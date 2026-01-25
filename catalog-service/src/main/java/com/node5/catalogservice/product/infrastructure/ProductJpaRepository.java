package com.node5.catalogservice.product.infrastructure;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductStatus;

import jakarta.transaction.Transactional;

public interface ProductJpaRepository extends JpaRepository<Product, UUID> {

	Page<Product> findByStatus(ProductStatus status, Pageable pageable);

	Page<Product> findByShopId(UUID shopId, Pageable pageable);

	Optional<Product> findByIdAndStatus(UUID id, ProductStatus status);

	List<Product> findByIdIn(Collection<UUID> ids);

	List<Product> findByIdInAndStatus(Collection<UUID> ids, ProductStatus status);

	@Query("select p.id from Product p where p.shopId in :shopIds")
	List<UUID> findIdsByShopIdIn(Collection<UUID> shopIds);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Transactional
	@Query("""
		update Product p
		   set p.status = 'DISCONTINUED'
		 where p.shopId = :shopId
		   and p.status <> 'DISCONTINUED'
	""")
	int discontinueByShopId(UUID shopId);
}
