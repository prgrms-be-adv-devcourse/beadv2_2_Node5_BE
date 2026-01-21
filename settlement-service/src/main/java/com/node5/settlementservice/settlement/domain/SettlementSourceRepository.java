package com.node5.settlementservice.settlement.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SettlementSourceRepository {
    List<SettlementSource> saveAll(List<SettlementSource> sources);

    List<UUID> findDistinctShopIds(LocalDateTime startDateTime, LocalDateTime endDateTime);

    void bulkUpdateStatus(List<UUID> shopIds, LocalDateTime startDate, LocalDateTime endDate);

    boolean existsByShopIdInAndStatus(List<UUID> shopIdList, SettlementProcessStatus processStatus);
}
