package com.node5.memberservice.member.application.event;

import java.util.List;
import java.util.UUID;

public record MemberDeletedEvent(
        UUID memberId,
        List<UUID> shopIds
){
}
