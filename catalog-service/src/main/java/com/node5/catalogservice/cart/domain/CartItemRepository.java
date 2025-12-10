package com.node5.catalogservice.cart.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartItemRepository {

	CartItem save(CartItem cartItem);

	Optional<CartItem> findById(UUID id);

	Page<CartItem> findByMemberId(UUID memberId, Pageable pageable);

	Optional<CartItem> findByMemberIdAndProductId(UUID memberId, UUID productId);

	void deleteById(UUID id);

	void deleteByMemberId(UUID memberId);
}
