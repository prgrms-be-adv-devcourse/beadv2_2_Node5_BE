package com.node5.catalogservice.client;

import java.util.UUID;

public interface ShopOwnershipPort {
	UUID getOwnerMemberId(UUID shopId);
}
