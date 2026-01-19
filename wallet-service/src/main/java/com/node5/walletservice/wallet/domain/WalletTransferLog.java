package com.node5.walletservice.wallet.domain;

import com.node5.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "예치금 이체 로그")
@Table(name = "\"wallet_transfer_log\"", schema = "wallet")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalletTransferLog extends BaseEntity {

    @Id
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID memberId;

    @Column(nullable = false)
    private String accountNo;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @Column(nullable = false)
    private LocalDateTime approvedAt;

    @Builder
    public WalletTransferLog(UUID memberId, String accountNo, Long amount, String transactionId, String message, LocalDateTime requestedAt, LocalDateTime approvedAt) {
        this.id = UUID.randomUUID();
        this.memberId = memberId;
        this.accountNo = accountNo;
        this.amount = amount;
        this.transactionId = transactionId;
        this.message = message;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }
}
