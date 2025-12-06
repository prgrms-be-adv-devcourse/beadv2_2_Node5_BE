package com.node5.walletservice.wallet.infrastructure;

import com.node5.walletservice.wallet.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletJpaRepository extends JpaRepository<Wallet, UUID> {
    Optional<Wallet> findByMemberId(UUID memberId);
}
