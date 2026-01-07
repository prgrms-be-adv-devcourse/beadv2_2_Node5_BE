package com.node5.catalogservice.cart.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.node5.catalogservice.cart.domain.CartItem;

import jakarta.transaction.Transactional;

public interface CartItemJpaRepository extends JpaRepository<CartItem, UUID> {

	Page<CartItem> findByCartId(UUID cartId, Pageable pageable);

	Optional<CartItem> findByCartIdAndProductId(UUID cartId, UUID productId);

	Optional<CartItem> findByIdAndCartId(UUID id, UUID cartId);

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("delete from CartItem c where c.cartId = :cartId")
	void deleteByCartId(UUID cartId);
}
