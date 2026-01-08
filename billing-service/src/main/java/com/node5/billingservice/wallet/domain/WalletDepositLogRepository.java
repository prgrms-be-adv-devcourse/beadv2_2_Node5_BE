package com.node5.billingservice.wallet.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface WalletDepositLogRepository {
    Page<WalletDepositLog> findAllByMemberId(UUID memberId, Pageable pageable);

    Boolean existsBySettlementId(UUID settlementId);

    WalletDepositLog save(WalletDepositLog walletDepositLog);
}
