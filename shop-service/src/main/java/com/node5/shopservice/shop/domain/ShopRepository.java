package com.node5.shopservice.shop.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ShopRepository {
    Page<Shop> findAllByMemberIdAndDeletedAtIsNull(UUID memberId, Pageable pageable);
    void save(Shop shop);
    Optional<Shop> findByIdAndMemberIdAndDeletedAtIsNull(UUID shopId, UUID memberId);
    int countByMemberIdAndDeletedAtIsNull(UUID memberId);
    void flush();
}
