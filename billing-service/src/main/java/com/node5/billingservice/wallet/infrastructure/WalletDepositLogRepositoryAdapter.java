package com.node5.billingservice.wallet.infrastructure;

import com.node5.billingservice.wallet.domain.WalletDepositLog;
import com.node5.billingservice.wallet.domain.WalletDepositLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WalletDepositLogRepositoryAdapter implements WalletDepositLogRepository {

    private final WalletDepositLogJpaRepository walletDepositLogJpaRepository;

    @Override
    public Page<WalletDepositLog> findAllByMemberId(UUID memberId, Pageable pageable) {
        return walletDepositLogJpaRepository.findAllByMemberId(memberId, pageable);
    }

    @Override
    public WalletDepositLog save(WalletDepositLog walletDepositLog) {
        return walletDepositLogJpaRepository.save(walletDepositLog);
    }
}
