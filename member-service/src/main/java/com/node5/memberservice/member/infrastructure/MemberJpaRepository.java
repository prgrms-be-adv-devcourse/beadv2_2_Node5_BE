package com.node5.memberservice.member.infrastructure;

import com.node5.memberservice.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberJpaRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findByIdAndDeletedAtIsNull(UUID id);

    Optional<Member> findByEmailAndDeletedAtIsNull(String email);

    Page<Member> findAllByIdNot(UUID id, Pageable pageable);
}

