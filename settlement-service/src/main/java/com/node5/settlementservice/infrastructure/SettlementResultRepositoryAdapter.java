package com.node5.settlementservice.infrastructure;

import com.node5.settlementservice.domain.SettlementResult;
import com.node5.settlementservice.domain.SettlementResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
