package com.node5.memberservice.member.domain;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository {
    Optional<Member> findById(UUID id);
    Optional<Member> findByEmail(String email);

    Member save(Member member);

    void deleteById(UUID id);
}
