package com.node5.shopservice.shop.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ShopRepository {
    Page<Shop> findAll(Pageable pageable);
    Shop save(Shop shop);
    Optional<Shop> findById(UUID id);
    void deleteById(UUID id);
}
