package com.node5.catalogservice.product.domain;

import java.math.BigDecimal;
import java.util.UUID;

import com.node5.catalogservice.product.exception.ProductErrorCode;
import com.node5.common.domain.BaseEntity;
import com.node5.common.exception.BaseException;

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
@Table(name = "product", schema = "catalog")
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
	@Column(nullable = false)
	private ProductStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProductCategory category;

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
		ProductCategory category,
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
		ProductCategory category,
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

		validateStockNonNegative(stock);
	}

	public void applyUpdate(
		String name,
		String description,
		BigDecimal price,
		Integer stock,
		ProductCategory category,
		String thumbnailUrl
	) {
		if (this.status == ProductStatus.DISCONTINUED) {
			throw new BaseException(ProductErrorCode.PRODUCT_STATUS_CHANGE_NOT_ALLOWED);
		}

		this.name = name;
		this.description = description;
		this.price = price;

		validateStockNonNegative(stock);
		this.stock = stock;

		this.category = category;
		this.thumbnailUrl = thumbnailUrl;
	}

	public void adjustStock(int newStock) {
		if (this.status == ProductStatus.DISCONTINUED) {
			throw new BaseException(ProductErrorCode.PRODUCT_STATUS_CHANGE_NOT_ALLOWED);
		}
		validateStockNonNegative(newStock);
		this.stock = newStock;
	}

	public void changeStatus(ProductStatus newStatus) {
		if (this.status == ProductStatus.DISCONTINUED || newStatus == ProductStatus.DISCONTINUED) {
			throw new BaseException(ProductErrorCode.PRODUCT_STATUS_CHANGE_NOT_ALLOWED);
		}
		this.status = newStatus;
	}

	public void discontinue() {
		if (this.status == ProductStatus.DISCONTINUED) {
			return;
		}
		this.status = ProductStatus.DISCONTINUED;
	}

	private void validateStockNonNegative(int stock) {
		if (stock < 0) {
			throw new BaseException(ProductErrorCode.PRODUCT_INVALID_STOCK);
		}
	}
}
