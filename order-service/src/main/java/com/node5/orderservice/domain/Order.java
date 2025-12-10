package com.node5.orderservice.domain;

import com.node5.common.domain.BaseEntity;
import com.node5.orderservice.application.dto.OrderCommand;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "\"order\"", schema = "\"order\"")
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
    private LocalDateTime paidAt; //결제 일시

    @Column
    private LocalDateTime closedAt; //취소, 환불, 배송 완료 일시

    @Column
    private UUID subscriptionId;

    @Column
    private BigDecimal totalAmount; //총 주문 금액

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
            BigDecimal totalAmount,
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
            BigDecimal totalAmount
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
