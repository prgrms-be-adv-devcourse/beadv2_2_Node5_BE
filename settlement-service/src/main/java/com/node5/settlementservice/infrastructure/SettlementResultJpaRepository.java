package com.node5.settlementservice.infrastructure;

import com.node5.settlementservice.domain.SettlementResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SettlementResultJpaRepository extends JpaRepository<SettlementResult, UUID> {

}
