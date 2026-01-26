package com.node5.memberservice.settlement.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "\"settlement_result\"", schema = "\"member\"")
public class SettlementResult {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID shopId;

    @Column(nullable = false)
    private LocalDate targetStartDate;
    @Column(nullable = false)
    private LocalDate targetEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SettlementPayoutStatus status;

    @Column(nullable = false)
    private BigDecimal salesAmount; // 총 매출액
    @Column(nullable = false)
    private BigDecimal feeRate; // 수수료율
    @Column(nullable = false)
    private BigDecimal feeAmount; // 수수료 금액
    @Column(nullable = false)
    private BigDecimal payoutAmount; // 최종 정산 금액

    @Column(nullable = false)
    private Long batchId;
    @Column(nullable = false)
    private LocalDateTime settledAt;

    private LocalDateTime payoutAt;
    private String errorMsg;

    protected SettlementResult() { }

    @Builder
    private SettlementResult(
            UUID shopId,
            LocalDate startDate,
            LocalDate endDate,
            Long batchId,
            LocalDateTime settledAt,
            BigDecimal salesAmount,
            BigDecimal feeRate,
            BigDecimal feeAmount,
            BigDecimal payoutAmount,
            SettlementPayoutStatus status
    ) {
        this.id = UUID.randomUUID();
        this.shopId = shopId;
        this.targetStartDate = startDate;
        this.targetEndDate = endDate;
        this.batchId = batchId;
        this.settledAt = settledAt;
        this.salesAmount = salesAmount;
        this.feeRate = feeRate;
        this.feeAmount = feeAmount;
        this.payoutAmount = payoutAmount;
        this.status = status;
    }

    public static SettlementResult create(
            UUID shopId,
            LocalDate startDate,
            LocalDate endDate,
            Long batchId,
            LocalDateTime settledAt,
            BigDecimal salesAmount,
            BigDecimal feeRate,
            BigDecimal feeAmount,
            BigDecimal payoutAmount,
            SettlementPayoutStatus status
    ) {
        return SettlementResult.builder()
                .shopId(shopId)
                .startDate(startDate)
                .endDate(endDate)
                .batchId(batchId)
                .settledAt(settledAt)
                .salesAmount(salesAmount)
                .feeRate(feeRate)
                .feeAmount(feeAmount)
                .payoutAmount(payoutAmount)
                .status(status)
                .build();
    }

    public void markPaid(LocalDateTime payoutAt) {
        this.status = SettlementPayoutStatus.PAID;
        this.payoutAt = payoutAt;
    }

    public void markFailed(String errorMsg) {
        this.status = SettlementPayoutStatus.FAILED;
        this.errorMsg = errorMsg;
    }
}
