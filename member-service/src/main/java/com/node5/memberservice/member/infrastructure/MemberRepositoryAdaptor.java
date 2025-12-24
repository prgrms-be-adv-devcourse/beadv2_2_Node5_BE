package com.node5.memberservice.member.infrastructure;

import com.node5.memberservice.member.domain.Member;
import com.node5.memberservice.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryAdaptor implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Optional<Member> findByIdAndDeletedAtIsNull(UUID id) {
        return memberJpaRepository.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    public Optional<Member> findByEmailAndDeletedAtIsNull(String email) {
        return memberJpaRepository.findByEmailAndDeletedAtIsNull(email);
    }

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(member);
    }

    @Override
    public Page<Member> findAll(Pageable pageable) {
        return memberJpaRepository.findAll(pageable);
    }
}
