package com.node5.orderservice.order.domain;

import com.node5.common.domain.BaseEntity;
import com.node5.orderservice.order.application.dto.OrderItemCommand;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Entity
@Table(name = "\"order_item\"", schema = "\"order\"")
public class OrderItem extends BaseEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID productId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderProgress status;

    @Column(nullable = false)
    private String name;

    @Column
    private String imgUrl; // 대표 이미지 url

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal totalPrice; // 상품별 주문 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderItemSettlementStatus settlementStatus;

    protected OrderItem() { }

    @Builder
    private OrderItem(
            UUID id,
            UUID orderId,
            UUID productId,
            OrderProgress status,
            String name,
            String imgUrl,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal totalPrice,
            OrderItemSettlementStatus settlementStatus
    ) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.status = status;
        this.name = name;
        this.imgUrl = imgUrl;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.settlementStatus = settlementStatus;
    }

    public static OrderItem create(
            UUID orderId,
            OrderItemCommand command
    ) {
        return OrderItem.builder()
                .id(UUID.randomUUID())
                .orderId(orderId)
                .status(OrderProgress.PAID)
                .productId(command.productId())
                .name(command.name())
                .imgUrl(command.imgUrl())
                .unitPrice(command.unitPrice())
                .quantity(command.quantity())
                .totalPrice(command.totalPrice())
                .settlementStatus(OrderItemSettlementStatus.PENDING)
                .build();
    }

    public void updateStatus(OrderProgress status){
        this.status = status;
    }
}
