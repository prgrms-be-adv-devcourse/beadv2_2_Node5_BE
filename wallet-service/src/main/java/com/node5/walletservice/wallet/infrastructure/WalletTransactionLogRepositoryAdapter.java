package com.node5.walletservice.wallet.infrastructure;


import com.node5.walletservice.wallet.domain.WalletTransactionLog;
import com.node5.walletservice.wallet.domain.WalletTransactionLogRepository;
import com.node5.walletservice.wallet.domain.WalletTransactionLogStatus;
import com.node5.walletservice.wallet.domain.WalletTransactionLogType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.node5.walletservice.wallet.domain.WalletTransactionLogGroupType.*;

@Repository
@RequiredArgsConstructor
public class WalletTransactionLogRepositoryAdapter implements WalletTransactionLogRepository {
    private final WalletTransactionLogJpaRepository walletTransactionLogJpaRepository;

    @Override
    public Page<WalletTransactionLog> findAllByMemberId(UUID memberId, Pageable pageable) {
        return walletTransactionLogJpaRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId, pageable);
    }

    @Override
    public Page<WalletTransactionLog> findInLogByMemberId(UUID memberId, Pageable pageable) {
        return walletTransactionLogJpaRepository.findAllByMemberIdAndGroupTypeOrderByCreatedAtDesc(memberId, IN, pageable);
    }

    @Override
    public Page<WalletTransactionLog> findOutLogByMemberId(UUID memberId, Pageable pageable) {
        return walletTransactionLogJpaRepository.findAllByMemberIdAndGroupTypeOrderByCreatedAtDesc(memberId, OUT, pageable);
    }

    @Override
    public int updateStatusByTransactionId(UUID memberId, String referenceId, WalletTransactionLogType type, WalletTransactionLogStatus oldStatus, WalletTransactionLogStatus newStatus) {
        return walletTransactionLogJpaRepository.updateStatus(memberId, referenceId, type, oldStatus, newStatus);
    }

    @Override
    public WalletTransactionLog save(WalletTransactionLog walletTransactionLog) {
        return walletTransactionLogJpaRepository.saveAndFlush(walletTransactionLog);
    }
}
