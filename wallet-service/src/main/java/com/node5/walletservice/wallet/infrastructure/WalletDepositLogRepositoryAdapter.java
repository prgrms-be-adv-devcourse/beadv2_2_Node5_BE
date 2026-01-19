package com.node5.walletservice.wallet.infrastructure;

import com.node5.walletservice.wallet.domain.WalletDepositLog;
import com.node5.walletservice.wallet.domain.WalletDepositLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WalletDepositLogRepositoryAdapter implements WalletDepositLogRepository {

    private final WalletDepositLogJpaRepository walletDepositLogJpaRepository;

    @Override
    public Boolean existsBySettlementId(UUID settlementId) {
        return walletDepositLogJpaRepository.existsBySettlementId(settlementId);
    }

    @Override
    public WalletDepositLog save(WalletDepositLog walletDepositLog) {
        return walletDepositLogJpaRepository.saveAndFlush(walletDepositLog);
    }
}
