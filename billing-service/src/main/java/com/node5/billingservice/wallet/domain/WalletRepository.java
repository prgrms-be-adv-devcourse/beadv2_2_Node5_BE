package com.node5.billingservice.wallet.domain;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository {

    Optional<Wallet> findByMemberId(UUID memberId);

    Optional<Wallet> findByMemberIdForUpdate(UUID memberId);

    Wallet save(Wallet wallet);
}
