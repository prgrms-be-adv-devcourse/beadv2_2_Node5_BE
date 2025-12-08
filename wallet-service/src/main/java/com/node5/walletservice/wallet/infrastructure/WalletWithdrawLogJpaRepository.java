package com.node5.walletservice.wallet.infrastructure;

import com.node5.walletservice.wallet.domain.WalletWithdrawLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletWithdrawLogJpaRepository extends JpaRepository<WalletWithdrawLog, UUID> {
    Page<WalletWithdrawLog> findAllByMemberId(UUID memberId, Pageable pageable);
}
