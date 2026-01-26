package com.node5.catalogservice.inventory.domain;

import java.util.UUID;

import com.node5.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "stock_reservation", schema = "catalog")
public class StockReservation extends BaseEntity {

	@Id
	private UUID id;

	@Column(name = "order_id", nullable = false)
	private UUID orderId;

	@Column(name = "product_id", nullable = false)
	private UUID productId;

	@Column(nullable = false)
	private int quantity;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ReservationStatus status;

	protected StockReservation() {
	}

	private StockReservation(UUID id, UUID orderId, UUID productId, int quantity, ReservationStatus status) {
		this.id = id;
		this.orderId = orderId;
		this.productId = productId;
		this.quantity = quantity;
		this.status = status;
	}

	public static StockReservation held(UUID orderId, UUID productId, int quantity) {
		return new StockReservation(UUID.randomUUID(), orderId, productId, quantity, ReservationStatus.HELD);
	}
}
