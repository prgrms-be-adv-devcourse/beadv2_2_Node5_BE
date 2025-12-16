package com.node5.settlementservice.settlement.infrastructure;

import com.node5.settlementservice.settlement.domain.SettlementSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SettlementSourceJpaRepository extends JpaRepository<SettlementSource, UUID> {

    @Query("SELECT s FROM SettlementSource s " +
            "WHERE s.shopId = :shopId " +
            "AND s.status = 'PENDING' " +
            "AND s.paidAt >= :startDate AND s.paidAt < :endDatePlusOneDay") // 종료일 다음 날의 00:00:00 미만
    List<SettlementSource> findPendingByShopAndPeriod(
            @Param("shopId") UUID shopId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDatePlusOneDay") LocalDateTime endDatePlusOneDay
    );

    @Query("SELECT s FROM SettlementSource s " +
            "WHERE s.status = 'PENDING' " +
            "AND s.paidAt >= :startDate AND s.paidAt < :endDatePlusOneDay") // 종료일 다음 날의 00:00:00 미만
    List<SettlementSource> findPendingByPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDatePlusOneDay") LocalDateTime endDatePlusOneDay
    );

    @Query("SELECT DISTINCT s.shopId FROM SettlementSource s " +
            "WHERE s.paidAt >= :startDateTime AND s.paidAt < :endDateTime " +
            "AND s.status = 'PENDING'")
    List<UUID> findDistinctShopIds(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
