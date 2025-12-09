package com.node5.memberservice.auth.infrastructure;

import com.node5.memberservice.auth.domain.OAuth;
import com.node5.memberservice.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OAuthJpaRepository extends JpaRepository<OAuth, UUID> {
    Optional<OAuth> findByProviderAndProviderId(String provider, String providerId);

    Optional<OAuth> findByProviderAndMember(String provider, Member member);
}
