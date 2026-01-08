package com.node5.billingservice.wallet.infrastructure;

import com.node5.billingservice.wallet.domain.WalletTransferLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletTransferLogJpaRepository extends JpaRepository<WalletTransferLog, Long> {

    Page<WalletTransferLog> findAllByMemberId(UUID memberId, Pageable pageable);
}
