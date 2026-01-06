package com.node5.shopservice.shop.application.event;

import java.util.UUID;

public record ShopDeletedEvent(
        UUID shopId
){
}
