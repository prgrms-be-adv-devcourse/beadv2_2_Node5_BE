package com.node5.billingservice.wallet.infrastructure;

import com.node5.billingservice.wallet.domain.WalletDepositLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletDepositLogJpaRepository extends JpaRepository<WalletDepositLog, UUID> {
    Page<WalletDepositLog> findAllByMemberId(UUID memberId, Pageable pageable);
}
