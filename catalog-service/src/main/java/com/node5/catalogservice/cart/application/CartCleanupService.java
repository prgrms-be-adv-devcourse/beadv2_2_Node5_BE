package com.node5.catalogservice.cart.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.node5.catalogservice.cart.domain.CartItemRepository;
import com.node5.catalogservice.cart.domain.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartCleanupService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;

	@Transactional
	public boolean cleanupByMemberId(UUID memberId) {
		return cartRepository.findByMemberId(memberId)
			.map(cart -> {
				cartItemRepository.deleteByCartId(cart.getId());
				cartRepository.delete(cart);
				return true;
			})
			.orElse(false);
	}
}
