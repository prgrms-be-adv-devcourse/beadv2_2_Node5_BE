package com.node5.catalogservice.product.domain;

import java.math.BigDecimal;
import java.util.UUID;

import com.node5.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "product")
public class Product extends BaseEntity {

	@Id
	private UUID id;

	@Column(name = "shop_id", nullable = false)
	private UUID shopId;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(columnDefinition = "text")
	private String description;

	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal price;

	@Column(nullable = false)
	private Integer stock;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ProductStatus status; // ON_SALE / HIDDEN / DISCONTINUED

	@Column(length = 50)
	private String category;

	@Column(name = "thumbnail_url")
	private String thumbnailUrl;

	protected Product() {
	}

	private Product(
		UUID id,
		UUID shopId,
		String name,
		String description,
		BigDecimal price,
		Integer stock,
		ProductStatus status,
		String category,
		String thumbnailUrl
	) {
		this.id = id;
		this.shopId = shopId;
		this.name = name;
		this.description = description;
		this.price = price;
		this.stock = stock;
		this.status = status;
		this.category = category;
		this.thumbnailUrl = thumbnailUrl;
	}

	public static Product create(
		UUID shopId,
		String name,
		String description,
		BigDecimal price,
		Integer stock,
		ProductStatus status,
		String category,
		String thumbnailUrl
	) {

		return new Product(
			null,
			shopId,
			name,
			description,
			price,
			stock,
			status,
			category,
			thumbnailUrl
		);
	}

	@PrePersist
	private void onCreate() {
		if (id == null) id = UUID.randomUUID();
		if (status == null) status = ProductStatus.ON_SALE;
		if (stock == null) stock = 0;
	}

	public void applyPatch(
		String name,
		String description,
		BigDecimal price,
		Integer stock,
		String category,
		String thumbnailUrl
	) {

		if (name != null) this.name = name;
		if (description != null) this.description = description;
		if (price != null) this.price = price;
		if (stock != null) this.stock = stock;
		if (category != null) this.category = category;
		if (thumbnailUrl != null) this.thumbnailUrl = thumbnailUrl;
	}

	public void changeStatus(ProductStatus newStatus) {
		if (this.status == ProductStatus.DISCONTINUED) {
			throw new IllegalStateException("이미 중단된 상품은 상태를 변경할 수 없습니다.");
		}
		this.status = newStatus;
	}

	public void discontinue() {
		if (this.status == ProductStatus.DISCONTINUED) {
			return;
		}
		this.status = ProductStatus.DISCONTINUED;
	}
}
