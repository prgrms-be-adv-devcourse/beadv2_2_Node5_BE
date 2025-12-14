package com.node5.settlementservice.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SettlementSourceRepository {
    List<SettlementSource> findPendingByShopAndPeriod(UUID shopId, LocalDateTime startDate, LocalDateTime endDate);

    List<SettlementSource> findPendingByPeriod(LocalDateTime startDate, LocalDateTime endDate);

    List<SettlementSource> saveAll(List<SettlementSource> sources);

    List<UUID> findDistinctShopIds(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
