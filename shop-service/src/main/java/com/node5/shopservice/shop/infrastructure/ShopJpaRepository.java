package com.node5.shopservice.shop.infrastructure;

import com.node5.shopservice.shop.domain.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShopJpaRepository extends JpaRepository<Shop, UUID> {
    Page<Shop> findAllByMemberIdAndDeletedAtIsNull(UUID memberId, Pageable pageable);
    int countByMemberIdAndDeletedAtIsNull(UUID memberId);
    Optional<Shop> findByIdAndMemberIdAndDeletedAtIsNull(UUID shopId, UUID memberId);
}
