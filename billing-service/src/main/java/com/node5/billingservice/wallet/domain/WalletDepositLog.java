package com.node5.billingservice.wallet.domain;

import com.node5.billingservice.wallet.exception.WalletException;
import com.node5.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.node5.billingservice.wallet.exception.WalletErrorCode.*;

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

    public void validateRefundable(String paymentKey, WalletDepositLogState state) {
        if (this.state == state) {
            throw new WalletException(REFUND_STATE_INVALID);
        }
        if (this.paymentKey == null) {
            throw new WalletException(PAYMENT_KEY_NOT_FOUND);
        }
        if (!this.paymentKey.equals(paymentKey)) {
            throw new WalletException(PAYMENT_KEY_MISMATCH);
        }
    }

    public void changeState(WalletDepositLogState state) {
        this.state = state;
    }

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
}
