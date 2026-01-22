package com.node5.memberservice.settlement.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

public interface SettlementResultRepository {

    Page<SettlementResult> findByShopIdAndTargetStartDateBetweenOrderByTargetEndDateDesc(UUID shopId, LocalDate periodStart, LocalDate periodEnd, Pageable pageable);
}
