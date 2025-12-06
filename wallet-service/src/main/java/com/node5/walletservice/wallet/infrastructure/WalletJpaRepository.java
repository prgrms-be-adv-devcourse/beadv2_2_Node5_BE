package com.node5.walletservice.wallet.infrastructure;

import com.node5.walletservice.wallet.domain.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface WalletJpaRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findByMemberId(UUID memberId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.memberId = :memberId")
    Optional<Wallet> findByMemberIdForUpdate(@Param("memberId") UUID memberId);
}
