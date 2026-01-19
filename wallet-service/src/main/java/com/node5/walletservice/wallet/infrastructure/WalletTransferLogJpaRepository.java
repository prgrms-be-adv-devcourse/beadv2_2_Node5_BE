package com.node5.walletservice.wallet.infrastructure;

import com.node5.walletservice.wallet.domain.WalletTransferLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletTransferLogJpaRepository extends JpaRepository<WalletTransferLog, Long> { }
