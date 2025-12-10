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
	public Optional<CartItem> findById(UUID id) {
		return cartItemJpaRepository.findById(id);
	}

	@Override
	public Page<CartItem> findByMemberId(UUID memberId, Pageable pageable) {
		return cartItemJpaRepository.findByMemberId(memberId, pageable);
	}

	@Override
	public Optional<CartItem> findByMemberIdAndProductId(UUID memberId, UUID productId) {
		return cartItemJpaRepository.findByMemberIdAndProductId(memberId, productId);
	}

	@Override
	public void deleteById(UUID id) {
		cartItemJpaRepository.deleteById(id);
	}

	@Override
	public void deleteByMemberId(UUID memberId) {
		cartItemJpaRepository.deleteByMemberId(memberId);
	}
}
