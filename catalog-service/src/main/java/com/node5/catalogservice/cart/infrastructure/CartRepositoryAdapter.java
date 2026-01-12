package com.node5.catalogservice.cart.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.node5.catalogservice.cart.domain.Cart;
import com.node5.catalogservice.cart.domain.CartRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CartRepositoryAdapter implements CartRepository {

	private final CartJpaRepository cartJpaRepository;

	@Override
	public Cart save(Cart cart) {
		return cartJpaRepository.save(cart);
	}

	@Override
	public Optional<Cart> findByMemberId(UUID memberId) {
		return cartJpaRepository.findByMemberId(memberId);
	}

	@Override
	public void delete(Cart cart) {
		cartJpaRepository.delete(cart);
	}
}
