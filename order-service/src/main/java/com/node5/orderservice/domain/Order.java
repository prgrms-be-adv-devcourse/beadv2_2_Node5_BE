package com.node5.orderservice.domain;

import com.node5.common.domain.BaseEntity;
import jakarta.persistence.*;
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

    @Column(nullable = false)
    private String orderNum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderType orderType;

    @Column
    private int totalAmount;

    @Column(nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String recipientAddress;

    protected Order() { }
}
