package com.node5.catalogservice.cart.domain;

import java.util.UUID;

import com.node5.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * 장바구니에 담긴 상품 항목을 나타내는 도메인 엔티티입니다.
 * <p>
 * - 사용자(memberId)와 상품(productId)의 관계를 표현합니다.<br>
 * - 수량(quantity)은 1 이상이어야 하며, 변경 시 도메인 규칙을 검증합니다.
 */
@Entity
@Table(name = "cart_item", schema = "catalog")
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

	/**
	 * 장바구니 항목을 생성합니다.
	 * <p>
	 * 수량은 1 이상이어야 하며, 유효하지 않으면 예외를 발생시킵니다.
	 */
	public static CartItem create(UUID memberId, UUID productId, Integer quantity) {
		validateQuantity(quantity);
		return new CartItem(null, memberId, productId, quantity);
	}

	/**
	 * 장바구니 항목의 수량을 변경합니다.
	 * <p>
	 * 수량은 1 이상이어야 합니다.
	 */
	public void updateQuantity(int quantity) {
		validateQuantity(quantity);
		this.quantity = quantity;
	}

	/**
	 * 기존 수량에 지정한 수량만큼을 증가시킵니다.
	 */
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
