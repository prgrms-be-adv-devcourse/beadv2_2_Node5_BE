package com.node5.catalogservice.inventory.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.node5.catalogservice.inventory.domain.ReservationStatus;
import com.node5.catalogservice.inventory.domain.StockReservation;

public interface StockReservationJpaRepository extends JpaRepository<StockReservation, UUID> {

	Optional<StockReservation> findByOrderIdAndProductId(UUID orderId, UUID productId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
		UPDATE StockReservation r
		   SET r.status = :toStatus
		 WHERE r.orderId = :orderId
		   AND r.productId = :productId
		   AND r.status = :fromStatus
	""")
	int updateStatus(
		@Param("orderId") UUID orderId,
		@Param("productId") UUID productId,
		@Param("fromStatus") ReservationStatus fromStatus,
		@Param("toStatus") ReservationStatus toStatus
	);
}
