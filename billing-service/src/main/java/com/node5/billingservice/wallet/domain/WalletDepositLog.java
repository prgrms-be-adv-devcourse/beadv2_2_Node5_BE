package com.node5.billingservice.wallet.domain;

import com.node5.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "예치금 입금 로그")
@Table(name = "\"wallet_deposit_log\"", schema = "billing")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalletDepositLog extends BaseEntity{

    @Id
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID memberId;

    @Column(nullable = false, unique = true)
    private UUID settlementId;

    @Column(nullable = false)
    private Long amount;

    @Builder
    public WalletDepositLog(UUID memberId, UUID settlementId, Long amount) {
        this.id = UUID.randomUUID();
        this.memberId = memberId;
        this.settlementId = settlementId;
        this.amount = amount;
    }
}
