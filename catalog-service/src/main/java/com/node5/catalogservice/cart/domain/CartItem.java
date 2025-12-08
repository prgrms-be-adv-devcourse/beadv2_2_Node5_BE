package com.node5.catalogservice.cart.domain;

import java.util.UUID;

import com.node5.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart_item")
public class CartItem extends BaseEntity {

	@Id
	private UUID id;

	@Column(name = "member_id", nullable = false)
	private UUID memberId;

	@Column(name = "product_id", nullable = false)
	private UUID productId;

	@Column(nullable = false)
	private Integer quantity;

	protected CartItem() {
	}

	protected CartItem(UUID id, UUID memberId, UUID productId, Integer quantity) {
		this.id = id;
		this.memberId = memberId;
		this.productId = productId;
		this.quantity = quantity;
	}

	public static CartItem create(UUID memberId, UUID productId, Integer quantity) {
		validateQuantity(quantity);
		return new CartItem(null, memberId, productId, quantity);
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
			throw new IllegalArgumentException("Quantity must be greater than zero.");
		}
	}

	@PrePersist
	public void onCreate() {
		if (id == null) id = UUID.randomUUID();
	}

	public UUID getId() {
		return id;
	}

	public UUID getMemberId() {
		return memberId;
	}

	public UUID getProductId() {
		return productId;
	}

	public Integer getQuantity() {
		return quantity;
	}
}
