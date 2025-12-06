package com.node5.walletservice.wallet.domain;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository {

    Optional<Wallet> findByMemberId(UUID memberId);

    Wallet save(Wallet wallet);
}
