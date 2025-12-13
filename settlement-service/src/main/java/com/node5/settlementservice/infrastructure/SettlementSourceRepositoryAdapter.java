package com.node5.settlementservice.infrastructure;

import com.node5.settlementservice.domain.SettlementSource;
import com.node5.settlementservice.domain.SettlementSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class SettlementSourceRepositoryAdapter implements SettlementSourceRepository {

    @Autowired
    private SettlementSourceJpaRepository sourceJpaRepository;

    @Override
    public List<SettlementSource> findPendingByShopAndPeriod(UUID shopId, LocalDateTime startDate, LocalDateTime endDate) {
        return sourceJpaRepository.findPendingByShopAndPeriod(shopId, startDate, endDate);
    }

    @Override
    public List<SettlementSource> findPendingByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return sourceJpaRepository.findPendingByPeriod(startDate, endDate);
    }

    @Override
    public List<SettlementSource> saveAll(List<SettlementSource> sources) {
        return sourceJpaRepository.saveAll(sources);
    }
}
