package com.node5.billingservice.wallet.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WalletWithdrawLogRepository {
    Page<WalletWithdrawLog> findAllByMemberId(UUID memberId, Pageable pageable);

    WalletWithdrawLog save(WalletWithdrawLog walletWithdrawLog);
}