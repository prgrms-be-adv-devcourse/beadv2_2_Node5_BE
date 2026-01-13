package com.node5.catalogservice.inventory.domain;

import java.util.UUID;

import com.node5.common.domain.BaseEntity;

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
}
