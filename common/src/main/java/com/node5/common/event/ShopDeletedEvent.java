package com.node5.common.event;

import java.util.UUID;

public record ShopDeletedEvent(
        UUID shopId
){
}
