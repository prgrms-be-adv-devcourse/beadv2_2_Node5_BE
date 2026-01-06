package com.node5.common.event;

import java.util.List;
import java.util.UUID;

public record MemberDeletedEvent(
        UUID memberId,
        List<UUID> shopIds
){
}
