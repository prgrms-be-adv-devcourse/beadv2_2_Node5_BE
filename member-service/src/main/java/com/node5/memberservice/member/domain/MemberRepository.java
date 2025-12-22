package com.node5.memberservice.member.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository {
    Optional<Member> findByIdAndDeletedAtIsNull(UUID id);
    Optional<Member> findByEmailAndDeletedAtIsNull(String email);

    Member save(Member member);
    Page<Member> findAll(Pageable pageable);
}
