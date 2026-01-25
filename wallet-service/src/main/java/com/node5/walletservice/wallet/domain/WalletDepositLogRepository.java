package com.node5.walletservice.wallet.domain;

import java.util.UUID;

public interface WalletDepositLogRepository {
    Boolean existsBySettlementId(UUID settlementId);

    WalletDepositLog save(WalletDepositLog walletDepositLog);
}
