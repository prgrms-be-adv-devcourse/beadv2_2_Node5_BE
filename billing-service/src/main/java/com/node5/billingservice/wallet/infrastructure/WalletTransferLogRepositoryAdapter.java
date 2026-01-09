package com.node5.billingservice.wallet.infrastructure;

import com.node5.billingservice.wallet.domain.WalletTransferLog;
import com.node5.billingservice.wallet.domain.WalletTransferLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WalletTransferLogRepositoryAdapter implements WalletTransferLogRepository {
    private final WalletTransferLogJpaRepository walletTransferLogJpaRepository;

    @Override
    public WalletTransferLog save(WalletTransferLog walletTransferLog) {
        return walletTransferLogJpaRepository.save(walletTransferLog);
    }
}
