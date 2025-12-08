package com.node5.subscriptionservice.subscription.domain;

import com.node5.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"subscription\"", schema = "public")
public class Subscription extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "price_per_item", nullable = false)
    private BigDecimal pricePerItem;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status", nullable = false)
    private SubscriptionStatus subscriptionStatus;

    @Column(name = "next_run_date", nullable = false)
    private LocalDate nextRunDate;

    @Column(length = 100, name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private Subscription(UUID id,
                        UUID memberId,
                        UUID productId,
                        BigDecimal pricePerItem,
                        Integer quantity,
                        BigDecimal totalPrice,
                        SubscriptionStatus subscriptionStatus,
                        LocalDate nextRunDate,
                        String deliveryAddress) {
        this.id = id;
        this.memberId = memberId;
        this.productId = productId;
        this.pricePerItem = pricePerItem;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.subscriptionStatus = subscriptionStatus;
        this.nextRunDate = nextRunDate;
        this.deliveryAddress = deliveryAddress;
        this.deletedAt = null;
    }

    public static Subscription create(UUID memberId,
                                UUID productId,
                                BigDecimal pricePerItem,
                                Integer quantity,
                                String deliveryAddress){
        validatePriceAndQuantity(pricePerItem, quantity);
        BigDecimal totalPrice = pricePerItem.multiply(BigDecimal.valueOf(quantity));
        return new Subscription(
                UUID.randomUUID(),
                memberId,
                productId,
                pricePerItem,
                quantity,
                totalPrice,
                SubscriptionStatus.ACTIVE,
                LocalDate.now().plusDays(1),
                deliveryAddress
        );
    }

    public void update(BigDecimal pricePerItem,
                                      Integer quantity,
                                      String deliveryAddress){
        SubscriptionStatus currentStatus = this.subscriptionStatus;
        // ACTIVE, PAUSED, FAILED 일때 수정 가능
        if (currentStatus == SubscriptionStatus.CANCELLED
                || currentStatus == SubscriptionStatus.UNAVAILABLE) {
            throw new IllegalStateException("Invalid state for update: " + currentStatus);
        }

        validatePriceAndQuantity(pricePerItem, quantity);

        BigDecimal totalPrice = pricePerItem.multiply(BigDecimal.valueOf(quantity));
        this.pricePerItem = pricePerItem;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.deliveryAddress = deliveryAddress;
    }

    public void calculateNextRunDate(List<SubscriptionRecurrenceRule> rules) {
        LocalDate today = LocalDate.now();

        this.nextRunDate = rules.stream()
                .map(rule -> rule.calculateNextRunDate(today))
                .min(LocalDate::compareTo)
                .orElseThrow(() -> new IllegalStateException("No recurrence rules found"));
    }

    public void pause(){
        SubscriptionStatus currentStatus = this.subscriptionStatus;
        // ACTIVE, FAILED 일때 정지 가능
        if (currentStatus == SubscriptionStatus.PAUSED
                || currentStatus == SubscriptionStatus.CANCELLED
                || currentStatus == SubscriptionStatus.UNAVAILABLE) {
            throw new IllegalStateException("Invalid state for pause: " + currentStatus);
        }
        this.subscriptionStatus = SubscriptionStatus.PAUSED;
    }

    public void resume() {
        SubscriptionStatus currentStatus = this.subscriptionStatus;
        // PAUSED 일때 재개 가능
        if (currentStatus == SubscriptionStatus.ACTIVE
                || currentStatus == SubscriptionStatus.FAILED
                || currentStatus == SubscriptionStatus.CANCELLED
                || currentStatus == SubscriptionStatus.UNAVAILABLE) {
            throw new IllegalStateException("Invalid state for resume: " + currentStatus);
        }
        this.subscriptionStatus = SubscriptionStatus.ACTIVE;
    }

    public void delete() {
        SubscriptionStatus currentStatus = this.subscriptionStatus;
        // ACTIVE, PAUSED, FAILED 일때 해지 가능
        if (currentStatus == SubscriptionStatus.CANCELLED
                || currentStatus == SubscriptionStatus.UNAVAILABLE) {
            throw new IllegalStateException("Invalid state for resume: " + currentStatus);
        }
        this.subscriptionStatus = SubscriptionStatus.CANCELLED;
    }

    private static void validatePriceAndQuantity(BigDecimal pricePerItem, Integer quantity) {
        if (pricePerItem == null || quantity == null) {
            throw new IllegalArgumentException("pricePerItem and quantity not included");
        }
        if (pricePerItem.signum() <= 0 || quantity <= 0) {
            throw new IllegalArgumentException("pricePerItem and quantity must more than 0");
        }
    }
}
