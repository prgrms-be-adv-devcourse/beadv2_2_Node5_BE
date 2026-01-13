package com.node5.catalogservice.inventory.infrastructure;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.node5.catalogservice.inventory.domain.Stock;

public interface StockJpaRepository extends JpaRepository<Stock, UUID> {

	boolean existsByProductId(UUID productId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
		UPDATE Stock s
		   SET s.quantity = s.quantity - :qty
		 WHERE s.productId = :productId
		   AND s.quantity >= :qty
	""")
	int decreaseIfEnough(@Param("productId") UUID productId, @Param("qty") int qty);
}
