package com.node5.memberservice.kafka.dto;

import java.util.List;
import java.util.UUID;

public record MemberDeletedEvent(
        UUID memberId,
        List<UUID> shopIds
){
}
