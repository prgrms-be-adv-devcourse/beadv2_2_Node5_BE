package com.node5.orderservice.domain;

import com.node5.common.domain.BaseEntity;
import com.node5.orderservice.application.dto.OrderCommand;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "\"order\"")
public class Order extends BaseEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(nullable = false, length = 20)
    private String orderNum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderType orderType;

    @Column
    private UUID subscriptionId;

    @Column
    private int totalAmount; //총 주문 금액

    @Column(nullable = false, length = 50)
    private String recipientName;

    @Column(nullable = false, length = 255)
    private String recipientAddress;

    protected Order() { }

    @Builder
    private Order(
            UUID id,
            UUID memberId,
            OrderStatus status,
            String orderNum,
            OrderType orderType,
            UUID subscriptionId,
            int totalAmount,
            String recipientName,
            String recipientAddress
    ) {
        this.id = id;
        this.memberId = memberId;
        this.status =  status;
        this.orderNum = orderNum;
        this.orderType = orderType;
        this.subscriptionId = subscriptionId;
        this.totalAmount = totalAmount;
        this.recipientName = recipientName;
        this.recipientAddress = recipientAddress;
    }

    public static Order create(
            OrderCommand command,
            String orderNum,
            int totalAmount
    ) {
        return Order.builder()
                .id(UUID.randomUUID())
                .status(OrderStatus.CREATED)
                .memberId(command.memberId())
                .orderNum(orderNum)
                .orderType(command.orderType())
                .subscriptionId(command.subscriptionId())
                .totalAmount(totalAmount)
                .recipientName(command.recipientName())
                .recipientAddress(command.recipientAddress())
                .build();
    }

}
