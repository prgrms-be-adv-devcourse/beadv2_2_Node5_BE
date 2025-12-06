package com.node5.walletservice.wallet.infrastructure;

import com.node5.walletservice.wallet.domain.WalletWithdrawLog;
import com.node5.walletservice.wallet.domain.WalletWithdrawLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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
    public WalletWithdrawLog save(WalletWithdrawLog walletWithdrawLog) {
        return walletWithdrawLogJpaRepository.save(walletWithdrawLog);
    }
}
