package com.node5.catalogservice.cart.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.node5.catalogservice.cart.domain.CartItem;

public interface CartItemJpaRepository extends JpaRepository<CartItem, UUID> {

	Page<CartItem> findByMemberId(UUID memberId, Pageable pageable);

	Optional<CartItem> findByMemberIdAndProductId(UUID memberId, UUID productId);

	void deleteByMemberId(UUID memberId);
}
