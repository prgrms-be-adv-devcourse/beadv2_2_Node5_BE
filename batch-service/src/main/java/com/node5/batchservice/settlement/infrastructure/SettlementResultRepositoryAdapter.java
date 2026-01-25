package com.node5.batchservice.settlement.infrastructure;

import com.node5.batchservice.settlement.domain.SettlementResultRepository;
import com.node5.memberservice.settlement.domain.SettlementResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SettlementResultRepositoryAdapter implements SettlementResultRepository {

    @Autowired
    private SettlementResultJpaRepository resultJpaRepository;

    @Override
    public List<SettlementResult> saveAll(List<SettlementResult> resultList) {
        return resultJpaRepository.saveAll(resultList);
    }

}
