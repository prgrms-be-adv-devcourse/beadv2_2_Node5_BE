package com.node5.memberservice.settlement.domain;

import java.util.List;
import java.util.UUID;

public interface SettlementSourceRepository {

    List<SettlementSource> saveAll(List<SettlementSource> sources);

    boolean existsByShopIdInAndStatus(List<UUID> shopIdList, SettlementProcessStatus processStatus);
}
