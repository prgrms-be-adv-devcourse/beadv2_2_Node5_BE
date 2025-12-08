package com.node5.shopservice.shop.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ShopRepository {
    Page<Shop> findAllByMemberId(UUID memberId, Pageable pageable);
    Shop save(Shop shop);
    Optional<Shop> findById(UUID id);
    void delete(Shop shop);
    int countByMemberId(UUID memberId);
}
