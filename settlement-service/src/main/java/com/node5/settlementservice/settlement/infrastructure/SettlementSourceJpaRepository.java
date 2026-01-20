package com.node5.settlementservice.settlement.infrastructure;

import com.node5.settlementservice.settlement.domain.SettlementProcessStatus;
import com.node5.settlementservice.settlement.domain.SettlementSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SettlementSourceJpaRepository extends JpaRepository<SettlementSource, UUID> {

    @Query("SELECT DISTINCT s.shopId FROM SettlementSource s " +
            "WHERE s.paidAt >= :startDateTime AND s.paidAt < :endDateTime " +
            "AND s.status = 'PENDING'")
    List<UUID> findDistinctShopIds(LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Modifying
    @Query("UPDATE SettlementSource s SET s.status = 'COMPLETED' " +
            "WHERE s.status = 'PENDING' " +
            "AND s.paidAt >= :start AND s.paidAt < :end " +
            "AND s.shopId IN :shopIds")
    void bulkUpdateStatus(
            @Param("shopIds") List<UUID> shopIds,
            @Param("start") LocalDateTime startDateTime,
            @Param("end") LocalDateTime endDateTime
    );

    boolean existsByShopIdInAndStatus(List<UUID> shopIdList, SettlementProcessStatus processStatus);
}
