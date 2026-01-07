package com.node5.catalogservice.cart.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartItemRepository {

	CartItem save(CartItem cartItem);

	Optional<CartItem> findById(UUID id);

	Page<CartItem> findByCartId(UUID cartId, Pageable pageable);

	Optional<CartItem> findByCartIdAndProductId(UUID cartId, UUID productId);

	Optional<CartItem> findByIdAndCartId(UUID id, UUID cartId);

	void deleteById(UUID id);

	void deleteByCartId(UUID cartId);
}
