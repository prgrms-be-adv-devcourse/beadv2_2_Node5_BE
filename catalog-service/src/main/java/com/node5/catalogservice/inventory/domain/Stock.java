package com.node5.catalogservice.inventory.domain;

import static com.node5.catalogservice.inventory.exception.InventoryErrorCode.*;

import java.util.UUID;

import com.node5.common.domain.BaseEntity;
import com.node5.common.exception.BaseException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "stock", schema = "catalog")
public class Stock extends BaseEntity {

	@Id
	@Column(name = "product_id")
	private UUID productId;

	@Column(nullable = false)
	private int quantity;

	protected Stock() {
	}

	public static Stock create(UUID productId, int quantity) {
		Stock stock = new Stock();
		stock.productId = productId;
		stock.quantity = quantity;
		return stock;
	}

	public void updateQuantity(int quantity) {
		if (quantity < 0) {
			throw new BaseException(INVALID_QUANTITY);
		}
		this.quantity = quantity;
	}
}
