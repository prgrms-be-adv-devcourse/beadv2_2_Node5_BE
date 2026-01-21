package com.node5.catalogservice.product.domain;

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
@Table(name = "product_idempotency", schema = "catalog")
public class ProductIdempotency extends BaseEntity {

	@Id
	@Column(name = "idempotency_key", length = 80)
	private String idempotencyKey;

	@Column(name = "product_id")
	private UUID productId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Status status;

	protected ProductIdempotency() {
	}

	private ProductIdempotency(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
		this.status = Status.PROCESSING;
	}

	public static ProductIdempotency processing(String key) {
		return new ProductIdempotency(key);
	}

	public void complete(UUID productId) {
		this.productId = productId;
		this.status = Status.COMPLETED;
	}

	public void fail() {
		this.status = Status.FAILED;
	}

	public enum Status {
		PROCESSING, COMPLETED, FAILED
	}
}
