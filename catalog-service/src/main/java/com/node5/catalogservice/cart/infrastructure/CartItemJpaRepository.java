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

	Page<CartItem> findByMemberId(UUID memberId, Pageable pageable);

	Optional<CartItem> findByMemberIdAndProductId(UUID memberId, UUID productId);

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("delete from CartItem c where c.memberId = :memberId")
	void deleteByMemberId(UUID memberId);
}
