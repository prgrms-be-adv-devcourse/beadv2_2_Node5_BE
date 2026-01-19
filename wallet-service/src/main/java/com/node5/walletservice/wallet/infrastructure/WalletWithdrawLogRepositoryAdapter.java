package com.node5.walletservice.wallet.infrastructure;

import com.node5.walletservice.wallet.domain.WalletWithdrawLog;
import com.node5.walletservice.wallet.domain.WalletWithdrawLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WalletWithdrawLogRepositoryAdapter implements WalletWithdrawLogRepository {
    private final WalletWithdrawLogJpaRepository walletWithdrawLogJpaRepository;

    @Override
    public Optional<WalletWithdrawLog> findByOrderId(UUID orderId) {
        return walletWithdrawLogJpaRepository.findByOrderId(orderId);
    }

    @Override
    public WalletWithdrawLog save(WalletWithdrawLog walletWithdrawLog) {
        return walletWithdrawLogJpaRepository.save(walletWithdrawLog);
    }
}
