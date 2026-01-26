package com.node5.batchservice.settlement.infrastructure;

import com.node5.batchservice.settlement.domain.SettlementSourceRepository;
import com.node5.memberservice.settlement.domain.SettlementProcessStatus;
import com.node5.memberservice.settlement.domain.SettlementSource;
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
    public List<UUID> findDistinctShopIds(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return sourceJpaRepository.findDistinctShopIds(startDateTime, endDateTime);
    }

    @Override
    public void bulkUpdateStatus(List<UUID> shopIds, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        sourceJpaRepository.bulkUpdateStatus(shopIds, startDateTime, endDateTime);
    }

}
