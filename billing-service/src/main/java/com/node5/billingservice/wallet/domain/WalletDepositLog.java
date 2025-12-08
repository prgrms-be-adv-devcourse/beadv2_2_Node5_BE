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
@Table(name = "\"wallet_deposit_log\"", schema = "public")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalletDepositLog extends BaseEntity{

    @Id
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID memberId;

    @Column(nullable = true, unique = true, updatable = false, length = 30)
    private String paymentKey;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletDepositLogState state;

    @Builder(builderMethodName = "paidBuilder", builderClassName = "PaidBuilder")
    public WalletDepositLog(UUID memberId, String paymentKey, Long amount) {
        this.id = UUID.randomUUID();
        this.memberId = memberId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.state = WalletDepositLogState.PAID;
    }

    @Builder(builderMethodName = "settledBuilder", builderClassName = "SettledBuilder")
    public WalletDepositLog(UUID memberId, Long amount) {
        this.id = UUID.randomUUID();
        this.memberId = memberId;
        this.paymentKey = null;
        this.amount = amount;
        this.state = WalletDepositLogState.SETTLED;
    }

    public void changeState(WalletDepositLogState state) {
        this.state = state;
    }
}
