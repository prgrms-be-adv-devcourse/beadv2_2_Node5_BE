package com.node5.walletservice.wallet.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(description = "지갑 출금 로그")
@Table(name = "\"wallet_withdraw_log\"", schema = "public")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalletWithdrawLog {

    @Id
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID memberId;

    @Column(nullable = false)
    private Long amount;

    @Builder
    public WalletWithdrawLog(UUID memberId, Long amount) {
        this.id = UUID.randomUUID();
        this.memberId = memberId;
        this.amount = amount;
    }
}
