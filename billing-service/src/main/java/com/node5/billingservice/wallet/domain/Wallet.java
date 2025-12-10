package com.node5.billingservice.wallet.domain;

import com.node5.billingservice.wallet.exception.WalletException;
import com.node5.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import static com.node5.billingservice.wallet.exception.WalletErrorCode.INSUFFICIENT_WALLET_BALANCE;

@Schema(description = "예치금")
@Table(name = "\"wallet\"", schema = "public")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet extends BaseEntity {

    // 예치금 ID
    @Id
    private UUID id;

    // 회원 ID
    @Column(nullable = false, unique = true, updatable = false)
    private UUID memberId;

    // 잔액
    @Column(nullable = false)
    private Long balance;

    public void deposit(Long amount) {
        this.balance += amount;
    }

    public void withdraw(Long amount) {
        if (amount > this.balance) {
            throw new WalletException(INSUFFICIENT_WALLET_BALANCE);
        }
        this.balance -= amount;
    }

    public void validateSufficientBalance(Long amount) {
        if (this.balance < amount) {
            throw new WalletException(INSUFFICIENT_WALLET_BALANCE);
        }
    }

    @Builder
    private Wallet(UUID memberId) {
        this.id = UUID.randomUUID();
        this.memberId = memberId;
        this.balance = 0L;
    }
}
