package com.node5.settlementservice.infrastructure;

import com.node5.settlementservice.domain.SettlementResult;
import com.node5.settlementservice.domain.SettlementResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class SettlementResultRepositoryAdapter implements SettlementResultRepository {

    @Autowired
    private SettlementResultJpaRepository resultJpaRepository;


    @Override
    public SettlementResult save(SettlementResult result) {
        return resultJpaRepository.save(result);
    }

    @Override
    public List<SettlementResult> saveAll(List<SettlementResult> resultList) {
        return resultJpaRepository.saveAll(resultList);
    }

    @Override
    public Page<SettlementResult> findByShopIdAndTargetStartDateBetweenOrderByTargetEndDateDesc(UUID shopId, LocalDate periodStart, LocalDate periodEnd, Pageable pageable) {
        return resultJpaRepository.findByShopIdAndTargetStartDateBetweenOrderByTargetEndDateDesc(shopId, periodStart, periodEnd, pageable);
    }
}
