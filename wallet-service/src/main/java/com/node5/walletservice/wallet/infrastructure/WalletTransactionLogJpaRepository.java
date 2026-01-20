package com.node5.walletservice.wallet.infrastructure;

import com.node5.walletservice.wallet.domain.WalletTransactionLog;
import com.node5.walletservice.wallet.domain.WalletTransactionLogGroupType;
import com.node5.walletservice.wallet.domain.WalletTransactionLogStatus;
import com.node5.walletservice.wallet.domain.WalletTransactionLogType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface WalletTransactionLogJpaRepository extends JpaRepository<WalletTransactionLog, UUID> {
    Page<WalletTransactionLog> findAllByMemberIdOrderByCreatedAtDesc(UUID memberId, Pageable pageable);

    Page<WalletTransactionLog> findAllByMemberIdAndGroupTypeOrderByCreatedAtDesc(UUID memberId, WalletTransactionLogGroupType groupType, Pageable pageable);

    @Modifying
    @Query("UPDATE WalletTransactionLog w SET w.status = :newStatus WHERE w.memberId = :memberId AND w.referenceId = :referenceId AND w.type = :type AND w.status = :oldStatus")
    int updateStatus(@Param("memberId") UUID memberId, @Param("referenceId") String referenceId, @Param("type") WalletTransactionLogType type, @Param("oldStatus") WalletTransactionLogStatus oldStatus, @Param("newStatus") WalletTransactionLogStatus newStatus);

    boolean existsByMemberIdAndReferenceIdAndStatus(UUID memberId, String referenceId, WalletTransactionLogStatus status);
}
