package com.node5.billingservice.wallet.infrastructure;

import com.node5.billingservice.wallet.domain.WalletTransferLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransferLogJpaRepository extends JpaRepository<WalletTransferLog, Long> { }
