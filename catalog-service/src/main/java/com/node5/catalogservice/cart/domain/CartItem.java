package com.node5.catalogservice.cart.domain;

import java.util.UUID;

import com.node5.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "cart_item", schema = "catalog")
public class CartItem extends BaseEntity {

	@Id
	private UUID id;

	@Column(name = "cart_id", nullable = false)
	private UUID cartId;

	@Column(name = "product_id", nullable = false)
	private UUID productId;

	@Column(nullable = false)
	private Integer quantity;

	protected CartItem() {
	}

	protected CartItem(UUID id, UUID cartId, UUID productId, Integer quantity) {
		this.id = id;
		this.cartId = cartId;
		this.productId = productId;
		this.quantity = quantity;
	}

	public static CartItem create(UUID cartId, UUID productId, Integer quantity) {
		validateQuantity(quantity);
		return new CartItem(null, cartId, productId, quantity);
	}

	public void updateQuantity(int quantity) {
		validateQuantity(quantity);
		this.quantity = quantity;
	}

	public void increaseQuantity(int amount) {
		validateQuantity(amount);
		this.quantity += amount;
	}

	private static void validateQuantity(int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
		}
	}

	@PrePersist
	public void onCreate() {
		if (id == null) id = UUID.randomUUID();
	}
}
