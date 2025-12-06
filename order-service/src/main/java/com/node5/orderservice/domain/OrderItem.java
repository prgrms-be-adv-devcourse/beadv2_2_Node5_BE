package com.node5.orderservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "\"order_item\"")
public class OrderItem {

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
    private int totalPrice;

    protected OrderItem() { }
}
