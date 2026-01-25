package com.node5.batchservice.settlement.infrastructure;

import com.node5.memberservice.settlement.domain.SettlementResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SettlementResultJpaRepository extends JpaRepository<SettlementResult, UUID> {
}
