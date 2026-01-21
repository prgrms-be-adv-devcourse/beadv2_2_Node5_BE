package com.node5.settlementservice.settlement.infrastructure;

import com.node5.settlementservice.settlement.domain.SettlementProcessStatus;
import com.node5.settlementservice.settlement.domain.SettlementSource;
import com.node5.settlementservice.settlement.domain.SettlementSourceRepository;
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
    public List<SettlementSource> saveAll(List<SettlementSource> sources) {
        return sourceJpaRepository.saveAll(sources);
    }

    @Override
    public List<UUID> findDistinctShopIds(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return sourceJpaRepository.findDistinctShopIds(startDateTime, endDateTime);
    }

    @Override
    public void bulkUpdateStatus(List<UUID> shopIds, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        sourceJpaRepository.bulkUpdateStatus(shopIds, startDateTime, endDateTime);
    }

    @Override
    public boolean existsByShopIdInAndStatus(List<UUID> shopIdList, SettlementProcessStatus processStatus) {
        return sourceJpaRepository.existsByShopIdInAndStatus(shopIdList, processStatus);
    }
}
