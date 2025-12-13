package com.node5.settlementservice.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "\"settlement_source\"", schema = "\"settlement\"")
public class SettlementSource {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private UUID shopId;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private BigDecimal itemAmount;

    @Column(nullable = false)
    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementProcessStatus status;

    protected SettlementSource() { }

    @Builder
    private SettlementSource(
            UUID productId,
            UUID shopId,
            UUID orderId,
            BigDecimal itemAmount,
            LocalDateTime paidAt,
            SettlementProcessStatus status
    ) {
        this.id = UUID.randomUUID();
        this.productId = productId;
        this.shopId = shopId;
        this.orderId = orderId;
        this.itemAmount = itemAmount;
        this.paidAt = paidAt;
        this.status = status;
    }

    public static SettlementSource create(
            UUID productId,
            UUID shopId,
            UUID orderId,
            BigDecimal itemAmount,
            LocalDateTime paidAt,
            SettlementProcessStatus status
    ) {
        return SettlementSource.builder()
                .productId(productId)
                .shopId(shopId)
                .orderId(orderId)
                .itemAmount(itemAmount)
                .paidAt(paidAt)
                .status(status)
                .build();
    }

    public void markCompleted() {
        this.status = SettlementProcessStatus.COMPLETED;
    }
}
