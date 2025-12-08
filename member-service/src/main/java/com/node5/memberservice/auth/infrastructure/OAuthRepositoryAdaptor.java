package com.node5.memberservice.auth.infrastructure;

import com.node5.memberservice.auth.domain.OAuth;
import com.node5.memberservice.auth.domain.OAuthRepository;
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
}
