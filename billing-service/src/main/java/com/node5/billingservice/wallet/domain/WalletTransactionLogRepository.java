package com.node5.billingservice.wallet.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WalletTransactionLogRepository {
    Page<WalletTransactionLog> findAllByMemberId(UUID memberId, Pageable pageable);

    Page<WalletTransactionLog> findInLogByMemberId(UUID memberId, Pageable pageable);

    Page<WalletTransactionLog> findOutLogByMemberId(UUID memberId, Pageable pageable);

    int updateStatusByTransactionId(UUID memberId, String referenceId, WalletTransactionLogType type, WalletTransactionLogStatus oldStatus, WalletTransactionLogStatus newStatus);

    WalletTransactionLog save(WalletTransactionLog walletTransactionLog);


}
