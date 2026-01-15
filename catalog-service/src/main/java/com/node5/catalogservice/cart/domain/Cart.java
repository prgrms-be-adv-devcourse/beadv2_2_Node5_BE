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
@Table(name = "cart", schema = "catalog")
public class Cart extends BaseEntity {

	@Id
	private UUID id;

	@Column(name = "member_id", nullable = false, unique = true)
	private UUID memberId;

	protected Cart() {}

	private Cart(UUID id, UUID memberId) {
		this.id = id;
		this.memberId = memberId;
	}

	public static Cart create(UUID memberId) {
		return new Cart(null, memberId);
	}

	@PrePersist
	public void onCreate() {
		if (id == null) id = UUID.randomUUID();
	}
}
