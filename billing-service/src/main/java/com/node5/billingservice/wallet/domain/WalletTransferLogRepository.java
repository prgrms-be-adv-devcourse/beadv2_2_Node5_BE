package com.node5.billingservice.wallet.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WalletTransferLogRepository {

    Page<WalletTransferLog> findAllByMemberId(UUID memberId, Pageable pageable);

    WalletTransferLog save(WalletTransferLog walletTransferLog);
}
