package com.node5.memberservice.settlement.infrastructure;

import com.node5.memberservice.settlement.domain.SettlementProcessStatus;
import com.node5.memberservice.settlement.domain.SettlementSource;
import com.node5.memberservice.settlement.domain.SettlementSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
    public boolean existsByShopIdInAndStatus(List<UUID> shopIdList, SettlementProcessStatus processStatus) {
        return sourceJpaRepository.existsByShopIdInAndStatus(shopIdList, processStatus);
    }
}
