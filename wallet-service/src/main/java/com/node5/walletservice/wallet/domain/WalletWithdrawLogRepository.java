package com.node5.walletservice.wallet.domain;

import java.util.Optional;
import java.util.UUID;

public interface WalletWithdrawLogRepository {
    Optional<WalletWithdrawLog> findByOrderId(UUID orderId);

    WalletWithdrawLog save(WalletWithdrawLog walletWithdrawLog);
}