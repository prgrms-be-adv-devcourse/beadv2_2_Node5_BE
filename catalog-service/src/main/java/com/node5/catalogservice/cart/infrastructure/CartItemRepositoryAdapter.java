package com.node5.catalogservice.cart.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.node5.catalogservice.cart.domain.CartItem;
import com.node5.catalogservice.cart.domain.CartItemRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CartItemRepositoryAdapter implements CartItemRepository {

	private final CartItemJpaRepository cartItemJpaRepository;

	@Override
	public CartItem save(CartItem cartItem) {
		return cartItemJpaRepository.save(cartItem);
	}

	@Override
	public Page<CartItem> findByCartId(UUID cartId, Pageable pageable) {
		return cartItemJpaRepository.findByCartId(cartId, pageable);
	}

	@Override
	public Optional<CartItem> findByCartIdAndProductId(UUID cartId, UUID productId) {
		return cartItemJpaRepository.findByCartIdAndProductId(cartId, productId);
	}

	@Override
	public Optional<CartItem> findByIdAndCartId(UUID id, UUID cartId) {
		return cartItemJpaRepository.findByIdAndCartId(id, cartId);
	}

	@Override
	public void deleteById(UUID id) {
		cartItemJpaRepository.deleteById(id);
	}

	@Override
	public void deleteByCartId(UUID cartId) {
		cartItemJpaRepository.deleteByCartId(cartId);
	}
}
