package com.node5.memberservice.auth.infrastructure;

import com.node5.memberservice.auth.domain.OAuth;
import com.node5.memberservice.auth.domain.OAuthRepository;
import com.node5.memberservice.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OAuthRepositoryAdaptor implements OAuthRepository {
    private final OAuthJpaRepository oAuthJpaRepository;

    @Override
    public OAuth save(OAuth oAuth) {
        return oAuthJpaRepository.save(oAuth);
    }

    @Override
    public Optional<OAuth> findByProviderAndProviderId(String provider, String providerId) {
        return oAuthJpaRepository.findByProviderAndProviderId(provider, providerId);
    }

    @Override
    public Optional<OAuth> findByProviderAndMember(String provider, Member member) {
        return oAuthJpaRepository.findByProviderAndMember(provider, member);
    }

    @Override
    public void deleteByMember(Member member) {
        oAuthJpaRepository.deleteByMember(member);
    }

}
