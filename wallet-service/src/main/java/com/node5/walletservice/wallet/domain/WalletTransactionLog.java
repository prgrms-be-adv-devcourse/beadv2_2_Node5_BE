package com.node5.walletservice.wallet.domain;

import com.node5.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "예치금 거래 로그")
@Table(name = "\"wallet_transaction_log\"", schema = "wallet")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalletTransactionLog extends BaseEntity {

    @Id
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID memberId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WalletTransactionLogType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WalletTransactionLogGroupType groupType;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Long balanceAfter;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WalletTransactionLogStatus status;

    @Column(nullable = false)
    private String referenceId;

    @Builder
    public WalletTransactionLog(UUID memberId, WalletTransactionLogType type, WalletTransactionLogGroupType groupType, Long amount, Long balanceAfter, WalletTransactionLogStatus status, String referenceId) {
        this.id = UUID.randomUUID();
        this.memberId = memberId;
        this.type = type;
        this.groupType = groupType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.status = status;
        this.referenceId = referenceId;
    }
}
