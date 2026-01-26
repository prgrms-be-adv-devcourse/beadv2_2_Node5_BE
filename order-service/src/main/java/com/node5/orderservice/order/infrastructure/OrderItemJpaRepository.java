package com.node5.orderservice.order.infrastructure;

import com.node5.orderservice.order.domain.OrderItemSettlementStatus;
import com.node5.orderservice.order.domain.OrderItem;
import com.node5.orderservice.order.domain.OrderProgress;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findByOrderId(UUID orderId);

    List<OrderItem> findByOrderIdIn(List<UUID> orderIds);

    @Query("SELECT oi.productId " +
            "FROM OrderItem oi " +
            "WHERE oi.orderId IN :orderIds " +
            "ORDER BY oi.createdAt DESC")
    List<UUID> findRecentProductIds(@Param("orderIds") List<UUID> orderIds, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OrderItem oi " +
            "SET oi.status = :toStatus " +
            "WHERE oi.status = :fromStatus")
    void updateStatusByCreatedAtBefore(OrderProgress fromStatus, OrderProgress toStatus);

    @Query("SELECT oi.status " +
            "FROM OrderItem oi " +
            "WHERE oi.orderId = :orderId " +
            "AND oi.productId = :productId")
    Optional<OrderProgress> findStatusByOrderIdAndProductId(@Param("orderId") UUID orderId, @Param("productId") UUID productId);

    @Query("SELECT (count(oi) > 0) " +
            "FROM OrderItem oi " +
            "WHERE oi.orderId in (" +
                "SELECT o.id " +
                "FROM Order o " +
                "WHERE o.memberId = :memberId" +
            ") " +
            "AND oi.status NOT IN :doneStatuses")
    boolean existsInProgressByMemberId(@Param("memberId") UUID memberId, @Param("doneStatuses") Collection<OrderProgress> doneStatuses);

    List<OrderItem> findByStatus(OrderProgress status);

    List<OrderItem> findByStatusAndSettlementStatus(OrderProgress status, OrderItemSettlementStatus settlementStatus);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE OrderItem oi " +
            "SET oi.settlementStatus = :settlementStatus " +
            "WHERE oi.id IN :orderItemIds")
    void updateSettlementStatus(@Param("orderItemIds") List<UUID> orderItemIds, @Param("settlementStatus") OrderItemSettlementStatus settlementStatus);

    Boolean existsByProductIdInAndSettlementStatus(List<UUID> productIds, OrderItemSettlementStatus settlementStatus);

    Optional<OrderItem> findByOrderIdAndProductId(UUID orderId, UUID productId);
}
