package com.node5.memberservice.auth.domain;

import com.node5.memberservice.member.domain.Member;

import java.util.Optional;

public interface OAuthRepository {

    OAuth save(OAuth oAuth);

    Optional<OAuth> findByProviderAndProviderId(String provider, String providerId);

    Optional<OAuth> findByProviderAndMember(String provider, Member member);
}
