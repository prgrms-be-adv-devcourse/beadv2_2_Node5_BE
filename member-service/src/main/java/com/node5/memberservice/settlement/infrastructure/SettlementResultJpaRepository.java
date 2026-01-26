package com.node5.memberservice.settlement.infrastructure;

import com.node5.memberservice.settlement.domain.SettlementResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface SettlementResultJpaRepository extends JpaRepository<SettlementResult, UUID> {

    Page<SettlementResult> findByShopIdAndTargetStartDateBetweenOrderByTargetEndDateDesc(
            UUID shopId, LocalDate periodStart, LocalDate periodEnd, Pageable pageable
    );
}
