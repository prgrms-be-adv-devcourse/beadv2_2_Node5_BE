package com.node5.catalogservice.testsupport;

import java.lang.reflect.Field;
import java.util.UUID;

import com.node5.catalogservice.cart.domain.Cart;

public final class CartTestFactory {

	private CartTestFactory() {
	}

	public static Cart create(UUID cartId, UUID memberId) {
		Cart cart = Cart.create(memberId);
		setId(cart, cartId);
		return cart;
	}

	private static void setId(Cart cart, UUID id) {
		try {
			Field field = Cart.class.getDeclaredField("id");
			field.setAccessible(true);
			field.set(cart, id);
		} catch (Exception e) {
			throw new RuntimeException("테스트용 Cart.id 설정에 실패했습니다.", e);
		}
	}
}
