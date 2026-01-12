package com.node5.catalogservice.cart.domain;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository {

	Cart save(Cart cart);

	Optional<Cart> findByMemberId(UUID memberId);
}
