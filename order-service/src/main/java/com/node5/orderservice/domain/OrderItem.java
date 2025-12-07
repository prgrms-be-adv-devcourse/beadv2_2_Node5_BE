package com.node5.orderservice.domain;

import com.node5.common.domain.BaseEntity;
import com.node5.orderservice.application.dto.OrderItemCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "\"order_item\"")
public class OrderItem extends BaseEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Column
    private String name;

    @Column
    private int unitPrice;

    @Column
    private int quantity;

    @Column
    private int totalPrice; //상품별 주문 금액

    protected OrderItem() { }

    @Builder
    private OrderItem(
            UUID id,
            UUID orderId,
            String name,
            int unitPrice,
            int quantity,
            int totalPrice
    ) {
        this.id = id;
        this.orderId = orderId;
        this.name = name;
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
                .name(command.name())
                .unitPrice(command.unitPrice())
                .quantity(command.quantity())
                .totalPrice(command.totalPrice())
                .build();
    }
}
