package com.node5.billingservice.wallet.infrastructure;

import com.node5.billingservice.wallet.domain.WalletTransferLog;
import com.node5.billingservice.wallet.domain.WalletTransferLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WalletTransferLogRepositoryAdapter implements WalletTransferLogRepository {
    private final WalletTransferLogJpaRepository walletTransferLogJpaRepository;

    @Override
    public Page<WalletTransferLog> findAllByMemberId(UUID memberId, Pageable pageable) {
        return walletTransferLogJpaRepository.findAllByMemberId(memberId, pageable);
    }

    @Override
    public WalletTransferLog save(WalletTransferLog walletTransferLog) {
        return walletTransferLogJpaRepository.save(walletTransferLog);
    }
}
