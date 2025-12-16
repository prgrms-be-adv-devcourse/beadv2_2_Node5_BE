package com.node5.billingservice.wallet.infrastructure;

import com.node5.billingservice.wallet.domain.Wallet;
import com.node5.billingservice.wallet.domain.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WalletRepositoryAdapter implements WalletRepository {

    private final WalletJpaRepository walletJpaRepository;

    @Override
    public Optional<Wallet> findByMemberId(UUID memberId) {
        return walletJpaRepository.findByMemberId(memberId);
    }

    @Override
    public Optional<Wallet> findByMemberIdForUpdate(UUID memberId) {
        return walletJpaRepository.findByMemberIdForUpdate(memberId);
    }

    @Override
    public Wallet save(Wallet wallet) {
        return walletJpaRepository.save(wallet);
    }
}
