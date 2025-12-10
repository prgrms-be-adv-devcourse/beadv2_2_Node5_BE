package com.node5.orderservice.domain;

import com.node5.common.domain.BaseEntity;
import com.node5.orderservice.application.dto.OrderItemCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    protected OrderItem() { }

    @Builder
    private OrderItem(
            UUID id,
            UUID orderId,
            UUID productId,
            String name,
            String imgUrl,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal totalPrice
    ) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.name = name;
        this.imgUrl = imgUrl;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public static OrderItem create(
            UUID orderId,
            OrderItemCommand command
    ) {
        return OrderItem.builder()
                .id(UUID.randomUUID())
                .orderId(orderId)
                .productId(command.productId())
                .name(command.name())
                .imgUrl(command.imgUrl())
                .unitPrice(command.unitPrice())
                .quantity(command.quantity())
                .totalPrice(command.totalPrice())
                .build();
    }
}
