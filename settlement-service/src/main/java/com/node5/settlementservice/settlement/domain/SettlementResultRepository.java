package com.node5.settlementservice.settlement.domain;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface SettlementResultRepository {
    SettlementResult save(SettlementResult settlementResult);

    List<SettlementResult> saveAll(List<SettlementResult> resultList);

    Page<SettlementResult> findByShopIdAndTargetStartDateBetweenOrderByTargetEndDateDesc(UUID shopId, LocalDate periodStart, LocalDate periodEnd, Pageable pageable);
}
