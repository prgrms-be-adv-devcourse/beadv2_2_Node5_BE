package com.node5.billingservice.wallet.domain;

import com.node5.billingservice.wallet.exception.WalletException;
import com.node5.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

import static com.node5.billingservice.wallet.exception.WalletErrorCode.*;

@Schema(description = "지갑 출금 로그")
@Table(name = "\"wallet_withdraw_log\"", schema = "billing")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalletWithdrawLog extends BaseEntity {

    @Id
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID memberId;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletWithdrawLogState state;

    public void validateRefundable(UUID orderId, Long amount) {
        if (this.state != WalletWithdrawLogState.PAID) {
            throw new WalletException(WALLET_REFUND_STATE_INVALID);
        }
        if (!Objects.equals(this.orderId, orderId)) {
            throw new WalletException(WALLET_ORDER_ID_MISMATCH);
        }
        if (!Objects.equals(this.amount, amount)) {
            throw new WalletException(WALLET_REFUND_AMOUNT_INVALID);
        }
    }

    public void refund() {
        this.state = WalletWithdrawLogState.REFUNDED;
    }

    @Builder
    public WalletWithdrawLog(UUID memberId, UUID orderId, Long amount) {
        this.id = UUID.randomUUID();
        this.memberId = memberId;
        this.orderId = orderId;
        this.amount = amount;
        this.state = WalletWithdrawLogState.PAID;
    }
}
