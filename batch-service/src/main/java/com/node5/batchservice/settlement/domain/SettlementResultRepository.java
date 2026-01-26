package com.node5.batchservice.settlement.domain;

import com.node5.memberservice.settlement.domain.SettlementResult;

import java.util.List;

public interface SettlementResultRepository {

    List<SettlementResult> saveAll(List<SettlementResult> resultList);
}
