package com.node5.batchservice.settlement.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SettlementSourceRepository {

    List<UUID> findDistinctShopIds(LocalDateTime startDateTime, LocalDateTime endDateTime);

    void bulkUpdateStatus(List<UUID> shopIds, LocalDateTime startDate, LocalDateTime endDate);
}
