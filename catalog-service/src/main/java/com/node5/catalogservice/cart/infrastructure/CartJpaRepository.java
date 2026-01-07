package com.node5.catalogservice.cart.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.node5.catalogservice.cart.domain.Cart;

public interface CartJpaRepository extends JpaRepository<Cart, UUID> {

	Optional<Cart> findByMemberId(UUID memberId);
}
