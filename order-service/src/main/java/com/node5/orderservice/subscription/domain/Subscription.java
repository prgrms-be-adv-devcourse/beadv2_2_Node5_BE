package com.node5.orderservice.subscription.domain;

import com.node5.common.domain.BaseEntity;
import com.node5.orderservice.subscription.exception.SubscriptionException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.node5.orderservice.subscription.exception.SubscriptionErrorCode.SUBSCRIPTION_INVALID_STATE;
import static com.node5.orderservice.subscription.exception.SubscriptionErrorCode.SUBSCRIPTION_RULE_NOT_FOUND;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"subscription\"", schema = "order")
public class Subscription extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "shop_id", nullable = false)
    private UUID shopId;

    @Column(nullable = false, length = 100)
    private String productName;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

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

    @Column(name = "last_processed_run_date")
    private LocalDate lastProcessedRunDate;

    @Column(length = 100, name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private Subscription(UUID id,
                        UUID memberId,
                        UUID productId,
                        UUID shopId,
                        String productName,
                        String thumbnailUrl,
                        BigDecimal pricePerItem,
                        Integer quantity,
                        BigDecimal totalPrice,
                        SubscriptionStatus subscriptionStatus,
                        LocalDate nextRunDate,
                        String deliveryAddress) {
        this.id = id;
        this.memberId = memberId;
        this.productId = productId;
        this.shopId = shopId;
        this.productName = productName;
        this.thumbnailUrl = thumbnailUrl;
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
                                UUID shopId,
                                String productName,
                                String thumbnailUrl,
                                BigDecimal pricePerItem,
                                Integer quantity,
                                String deliveryAddress){
        BigDecimal totalPrice = pricePerItem.multiply(BigDecimal.valueOf(quantity));
        return new Subscription(
                UUID.randomUUID(),
                memberId,
                productId,
                shopId,
                productName,
                thumbnailUrl,
                pricePerItem,
                quantity,
                totalPrice,
                SubscriptionStatus.ACTIVE,
                LocalDate.now().plusDays(1),
                deliveryAddress
        );
    }

    public void update(String deliveryAddress){
        SubscriptionStatus currentStatus = this.subscriptionStatus;
        // ACTIVE, PAUSED, FAILED 일때 수정 가능
        if (currentStatus == SubscriptionStatus.CANCELLED
                || currentStatus == SubscriptionStatus.UNAVAILABLE) {
            throw new SubscriptionException(SUBSCRIPTION_INVALID_STATE);
        }
        if (deliveryAddress != null) {
            this.deliveryAddress = deliveryAddress;
        }
    }

    public void calculateNextRunDate(List<SubscriptionRecurrenceRule> rules, LocalDate baseDate) {
        this.nextRunDate = rules.stream()
                .map(rule -> rule.calculateNextRunDate(baseDate))
                .min(LocalDate::compareTo)
                .orElseThrow(() -> new SubscriptionException(SUBSCRIPTION_RULE_NOT_FOUND));
    }

    public void pause(){
        SubscriptionStatus currentStatus = this.subscriptionStatus;
        // ACTIVE, FAILED 일때 정지 가능
        if (currentStatus == SubscriptionStatus.PAUSED
                || currentStatus == SubscriptionStatus.CANCELLED
                || currentStatus == SubscriptionStatus.UNAVAILABLE) {
            throw new SubscriptionException(SUBSCRIPTION_INVALID_STATE);
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
            throw new SubscriptionException(SUBSCRIPTION_INVALID_STATE);
        }
        this.subscriptionStatus = SubscriptionStatus.ACTIVE;
    }

    public void cancel() {
        SubscriptionStatus currentStatus = this.subscriptionStatus;
        // ACTIVE, PAUSED, FAILED 일때 해지 가능
        if (currentStatus == SubscriptionStatus.CANCELLED
                || currentStatus == SubscriptionStatus.UNAVAILABLE) {
            throw new SubscriptionException(SUBSCRIPTION_INVALID_STATE);
        }
        this.subscriptionStatus = SubscriptionStatus.CANCELLED;
        this.deletedAt = LocalDateTime.now();
    }

    public void markFailed() {
        if (this.subscriptionStatus == SubscriptionStatus.CANCELLED
                || this.subscriptionStatus == SubscriptionStatus.UNAVAILABLE
                || this.subscriptionStatus == SubscriptionStatus.TERMINATED) {
            throw new SubscriptionException(SUBSCRIPTION_INVALID_STATE);
        }
        this.subscriptionStatus = SubscriptionStatus.FAILED;
    }

    public boolean terminate() {
        if (this.subscriptionStatus == SubscriptionStatus.TERMINATED) {
            return false;
        }
        this.subscriptionStatus = SubscriptionStatus.TERMINATED;
        if (deletedAt == null) {
            this.deletedAt = LocalDateTime.now();
        }
        return true;
    }
}
