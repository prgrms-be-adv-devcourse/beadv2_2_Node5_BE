package com.node5.billingservice.wallet.infrastructure;

import com.node5.billingservice.wallet.domain.WalletWithdrawLog;
import com.node5.billingservice.wallet.domain.WalletWithdrawLogRepository;
import com.node5.billingservice.wallet.domain.WalletWithdrawLogState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WalletWithdrawLogRepositoryAdapter implements WalletWithdrawLogRepository {
    private final WalletWithdrawLogJpaRepository walletWithdrawLogJpaRepository;

    @Override
    public Page<WalletWithdrawLog> findAllByMemberId(UUID memberId, Pageable pageable) {
        return walletWithdrawLogJpaRepository.findAllByMemberId(memberId, pageable);
    }

    @Override
    public Optional<WalletWithdrawLog> findByOrderId(UUID orderId) {
        return walletWithdrawLogJpaRepository.findByOrderId(orderId);
    }

    @Override
    public WalletWithdrawLog save(WalletWithdrawLog walletWithdrawLog) {
        return walletWithdrawLogJpaRepository.save(walletWithdrawLog);
    }
}
