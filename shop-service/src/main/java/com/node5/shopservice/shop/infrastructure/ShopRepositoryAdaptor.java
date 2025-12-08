package com.node5.shopservice.shop.infrastructure;

import com.node5.shopservice.shop.domain.Shop;
import com.node5.shopservice.shop.domain.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ShopRepositoryAdaptor implements ShopRepository {

    private final ShopJpaRepository shopJpaRepository;

    @Override
    public Page<Shop> findAllByMemberIdAndDeletedAtIsNull(UUID memberId, Pageable pageable) {
        return shopJpaRepository.findAllByMemberIdAndDeletedAtIsNull(memberId, pageable);
    }

    @Override
    public Shop save(Shop shop) {
        return shopJpaRepository.save(shop);
    }

    @Override
    public Optional<Shop> findByIdAndDeletedAtIsNull(UUID id) {
        return shopJpaRepository.findByIdAndDeletedAtIsNull(id);
    }


    @Override
    public int countByMemberIdAndDeletedAtIsNull(UUID memberId) {
        return shopJpaRepository.countByMemberIdAndDeletedAtIsNull(memberId);
    }

    @Override
    public void flush() {
        shopJpaRepository.flush();
    }
}
