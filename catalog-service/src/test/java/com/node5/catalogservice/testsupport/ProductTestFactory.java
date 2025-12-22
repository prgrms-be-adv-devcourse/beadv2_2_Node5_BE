package com.node5.catalogservice.testsupport;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.UUID;

import com.node5.catalogservice.product.domain.Product;
import com.node5.catalogservice.product.domain.ProductCategory;
import com.node5.catalogservice.product.domain.ProductStatus;

public final class ProductTestFactory {

	private ProductTestFactory() {
	}

	public static Product create(ProductStatus status) {
		return create(
			UUID.randomUUID(),
			UUID.randomUUID(),
			"name",
			"desc",
			BigDecimal.valueOf(1000),
			10,
			status,
			anyCategory(),
			"thumb.png"
		);
	}

	public static Product onSale() {
		return create(ProductStatus.ON_SALE);
	}

	public static Product hidden() {
		return create(ProductStatus.HIDDEN);
	}

	public static Product discontinued() {
		return create(ProductStatus.DISCONTINUED);
	}

	public static Product withId(UUID id, ProductStatus status) {
		return create(
			id,
			UUID.randomUUID(),
			"name",
			"desc",
			BigDecimal.valueOf(1000),
			10,
			status,
			anyCategory(),
			"thumb.png"
		);
	}

	public static Product create(
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
		Product product = Product.create(
			shopId,
			name,
			description,
			price,
			stock,
			status,
			category,
			thumbnailUrl
		);

		setId(product, id);

		return product;
	}

	private static ProductCategory anyCategory() {
		ProductCategory[] values = ProductCategory.values();
		if (values.length == 0) {
			throw new IllegalStateException("ProductCategory enum에 정의된 값이 없습니다.");
		}
		return values[0];
	}

	private static void setId(Product product, UUID id) {
		try {
			Field field = Product.class.getDeclaredField("id");
			field.setAccessible(true);
			field.set(product, id);
		} catch (Exception e) {
			throw new RuntimeException("테스트용 Product.id 설정에 실패했습니다.", e);
		}
	}
}
