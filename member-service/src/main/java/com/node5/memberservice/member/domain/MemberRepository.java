package com.node5.memberservice.member.domain;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository {
    Optional<Member> findByIdAndDeletedAtIsNull(UUID id);
    Optional<Member> findByEmailAndDeletedAtIsNull(String email);

    Member save(Member member);
}
