package com.node5.settlementservice.domain;

import java.util.List;

public interface SettlementResultRepository {
    SettlementResult save(SettlementResult settlementResult);

    List<SettlementResult> saveAll(List<SettlementResult> resultList);
}
